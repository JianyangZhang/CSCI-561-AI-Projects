#include <iostream>
#include <string>
#include <fstream>
#include <cstdlib>
#include <vector>
#include <algorithm>
#include <cctype>
#include <sstream>
#include <map>
#include <set>
#include <unordered_set>
#include <cassert>

using namespace std;


typedef string argument;
typedef vector<argument> arguments;
typedef pair<string, arguments> literal;
typedef set<literal> clause;


int clause_count = 0;

//ofstream flog{ "log.txt" };


bool is_variable(argument arg)
{
    return arg.length() > 0 && islower(arg[0]);
}


argument& standardize_variable(argument& arg, int cnt)
{
    if (is_variable(arg))
    {
        stringstream ss;
        ss << arg << cnt;
        arg = ss.str();
    }
    return arg;
}

literal& standardize_variable(literal& lit, int cnt)
{
    for (auto& arg : lit.second)
    {
        standardize_variable(arg, cnt);
    }
    return lit;
}

vector<string> split_string(string line, string del)
{
    vector<string> parts;

    auto pos = line.find(del);
    while (pos != string::npos)
    {
        parts.push_back(line.substr(0, pos));
        line = line.substr(pos + del.length());
        pos = line.find(del);
    }
    parts.push_back(line);

    return parts;
}

string& erase_space(string &s)
{
    s.erase(remove_if(s.begin(), s.end(), ::isspace), s.end());
    return s;
}

literal parse_literal(string line)
{
    literal lit;
    auto p = split_string(line, "(");
    lit.first = erase_space(p[0]);
    p = split_string(p[1], ")");
    lit.second = split_string(erase_space(p[0]), ",");
    return lit;
}

clause parse_clause(string line)
{
    clause cla;
    auto p = split_string(line, "|");
    for (auto& t: p)
    {
		auto lit = parse_literal(t);
        cla.insert(standardize_variable(lit, clause_count));
    }
    clause_count++;
    return cla;
}

literal negated(literal lit)
{
    if (lit.first[0] == '~')
        lit.first = lit.first.substr(1);
    else
        lit.first = "~" + lit.first;
    return lit;
}


struct knowledge
{
    vector<clause> clauses;
    map<string, set<int> > kb_index;
    map<int, set<int> > kb_checked;
    void tell(clause c)
    {
        clauses.push_back(c);
        for (auto it = c.begin(); it != c.end(); it++)
        {
            if (kb_index.find(it->first) != kb_index.end())
            {
                kb_index.at(it->first).insert(clauses.size() - 1);
            }
            else
            {
                kb_index[it->first] = set<int>{(int)clauses.size() - 1};
            }
        }
    }

    vector<pair<int, int> > get_resolvable() const
    {
        vector<pair<int, int> > resolvables;

        for (int i = 0; i < clauses.size(); i++)
        {
            set<int> idx;
            for (auto it = clauses[i].begin(); it != clauses[i].end(); it++)
            {
                auto lit = negated(*it);
                if (kb_index.find(lit.first) != kb_index.end())
                {
                    idx.insert(kb_index.at(lit.first).begin(), kb_index.at(lit.first).end());
                }
            }
            bool checked = kb_checked.find(i) != kb_checked.end();
            for (auto j : idx)
            {
                if (i < j)
                {
                    if (checked && kb_checked.at(i).find(i) != kb_checked.at(i).end())
                        continue;
                    assert(i < clauses.size() && j < clauses.size());
                    resolvables.push_back(pair<int, int>(i, j));
                }
            }
        }
        return resolvables;
    }
};



struct unification
{
    map<string, string> subst;
    bool failure = false;
};

unification& unify(arguments a1, arguments a2, unification& theta);
unification& unify_var(argument a1, argument a2, unification& theta)
{
    if (theta.subst.find(a1) != theta.subst.end())
    {
        a1 = theta.subst.at(a1);
        return unify(arguments{a1}, arguments{a2}, theta);
    }
    else if (theta.subst.find(a2) != theta.subst.end())
    {
        a2 = theta.subst.at(a2);
        return unify(arguments{a1}, arguments{a2}, theta);
    }
    else
    {
        theta.subst[a1] = a2;
        return theta;
    }
}

unification& unify(arguments a1, arguments a2, unification& theta)
{
    if (theta.failure)
        return theta;
    else if (a1 == a2)
        return theta;
    else if (a1.size() == 1 && is_variable(a1[0]))
    {
        return unify_var(a1[0], a2[0], theta);
    }
    else if (a2.size() == 1 && is_variable(a2[0]))
    {
        return unify_var(a2[0], a1[0], theta);
    }
    else if (a1.size() > 1 && a2.size() > 1)
    {
        argument arg1 = a1[0];
        argument arg2 = a2[0];
        a1.erase(a1.begin());
        a2.erase(a2.begin());
        return unify(a1, a2, unify(arguments{arg1}, arguments{arg2}, theta));
    }
    else
    {
        theta.failure = true;
        return theta;
    }
}


unification& unify(literal l1, literal l2, unification& theta)
{
    if (!(("~" + l1.first == l2.first) || ("~" + l2.first == l1.first)))
    {
        theta.failure = true;
        return theta;
    }
    return unify(l1.second, l2.second, theta);
}

literal& substitute(literal& lit, map<string, string>& subst)
{
    for (auto& a : lit.second)
    {
        if (subst.find(a) != subst.end())
        {
            a = subst.at(a);
        }
    }
    return lit;
}

bool check_clause(const clause& cla)
{
    for (auto it = cla.begin(); it != cla.end(); it++)
    {
        for (auto itt = next(it); itt != cla.end(); itt++)
        {
            if ((("~" + it->first == itt->first) || ("~" + itt->first == it->first))
                && it->second == itt->second)
                return false;
        }
    }
    return true;
}

bool resolve(clause c1, clause c2, vector<clause>& new_clauses)
{
    for (auto it1 = c1.begin(); it1 != c1.end(); it1++)
        for (auto it2 = c2.begin(); it2 != c2.end(); it2++)
        {
            unification theta;
            if (unify(*it1, *it2, theta).failure == false)
            {
                clause cla;
                for (auto it = c1.begin(); it != c1.end(); it++)
                {
                    if (*it != *it1)
                    {
                        literal l = *it;
                        cla.insert(substitute(l, theta.subst));
                    }
                }
                for (auto it = c2.begin(); it != c2.end(); it++)
                {
                    if (*it != *it2)
                    {
                        literal l = *it;
                        cla.insert(substitute(l, theta.subst));
                    }
                }
                if (cla.size() == 0)
                    return false;

                if (check_clause(cla) && find(new_clauses.begin(), new_clauses.end(), cla) == new_clauses.end())
                {
                    new_clauses.push_back(cla);
                }
            }
        }
    return true;
}


ostream& operator<<(ostream& os, const arguments& args)
{
    os << "(" << args[0];
    for (int i = 1; i < args.size(); i++)
        os << "," << args[i];
    os << ")";
    return os;
}

ostream& operator<<(ostream& os, const literal& lit)
{
    os << lit.first << lit.second;
    return os;
}

ostream& operator<<(ostream& os, const clause& cla)
{
    auto it = cla.begin();
    os << *it;
    for (it++; it != cla.end(); it++)
        os << " | " << *it;
    return os;
}

ostream& operator<<(ostream& os, const vector<clause>& clauses)
{
    for (auto& t: clauses)
    {
        os << t << endl;
    }
    return os;
}

bool resoultion(const knowledge& kb, literal query)
{
    clause cla;
    cla.insert(standardize_variable(query, clause_count++));
    knowledge new_kb = kb;
    new_kb.tell(cla);

    for (;;)
    {
        vector<clause> new_clauses;
        vector<pair<int, int>> resolvable_clauses = new_kb.get_resolvable();
        //flog << "================ new_kb: " << new_kb.clauses.size() << "===================" << endl;
        //flog << new_kb.clauses;
        //flog << "================ new_kb: " << new_kb.clauses.size() << "===================" << endl;
        for (auto& t : resolvable_clauses)
        {
            bool ret = resolve(new_kb.clauses[t.first], new_kb.clauses[t.second], new_clauses);
            if (new_kb.kb_checked.find(t.first) != new_kb.kb_checked.end())
                new_kb.kb_checked.at(t.first).insert(t.second);
            else
                new_kb.kb_checked[t.first] = set<int>{t.second};
            if (ret == false)
                return true;
        }

        //flog << "================ new_clauses: " << new_clauses.size() << "===================" << endl;
        //flog << new_clauses;
        //flog << "================ new_clauses: " << new_clauses.size() << "===================" << endl;

        bool flag = false;

        if (new_clauses.size() + new_kb.clauses.size() > 2000)
            return false;

        for (auto it = new_clauses.begin(); it != new_clauses.end(); it++)
        {
            if (find(new_kb.clauses.begin(), new_kb.clauses.end(), *it) == new_kb.clauses.end())
            {
                flag = true;
                new_kb.tell(*it);
            }
        }
        if (flag == false)
        {
            return false;
        }
    }

    return false;
}

int main()
{
    string input = "input.txt";
    ifstream in(input, ios_base::binary);

    string line;
    getline(in, line);

    int nq = stoi(line);
    vector<literal> queries;
    for (auto i = 0; i < nq; i++)
    {
        getline(in, line);
        queries.push_back(parse_literal(line));
    }

    knowledge kb;
    getline(in, line);
    int ns = stoi(line);
    for (auto i = 0; i < ns; i++)
    {
        getline(in, line);
        kb.tell(parse_clause(line));
    }

    vector<bool> answers;
    for (auto& q : queries)
    {
        answers.push_back(resoultion(kb, negated(q)));
    }

    ofstream out("output.txt");
    for (auto i = 0; i < answers.size(); i++)
    {
        if (i == 0)
            out << (answers[i] ? "TRUE" : "FALSE");
        else
            out << "\n" << (answers[i] ? "TRUE" : "FALSE");
    }
    return 0;
}