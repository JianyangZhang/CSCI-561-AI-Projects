import re
import copy

table_based_kb = dict()
new_kb = list()
duplicate_predicates = list()
actual_size = 0

def remove_useless_characters(formula):
	stack = list()
	formula = list(formula)
	new_formula = ""

	for i in range(0,len(formula)):
		if formula[i]=='(':
			if (i-1)>-1:
				if formula[i-1].isalpha()==False:
					stack.append(tuple(('(', i)))
			elif (i==0):
				stack.append(tuple(('(', i)))
		elif formula[i]=='&' or formula[i]=='|':
			stack.append(tuple((formula[i], i)))
		elif formula[i]==' ':
			formula[i]='@'
		elif formula[i]=='=' and (i+1)<len(formula):
			if formula[i+1]=='>':
				stack.append(tuple(("=>", i)))
		elif formula[i]==')':
			if (i-1)>-1:
				if formula[i-1].isalpha()==False:
					if len(stack)!=0:
						if stack[-1][0]=='(':
							formula[stack[-1][1]]='@'
							formula[i]='@'
							del stack[-1]
						else:
							j = len(stack)-1
							left_brack_flag = 0
							while(j>-1):
								if stack[j][0]!='(':
									del stack[j]
									j = len(stack)-1
								elif stack[j][0]=='(':
									left_brack_flag = 1
									del stack[j]
									break
							if left_brack_flag==0:
								formula[i]='@'
					else:
						formula[i]='@'

	for tup in stack:
		if tup[0]=='(':
			formula[tup[1]]='@'

	return "".join(formula).replace('@','')

def remove_implications(formula):
	stack = list()	
	temp_formula = ""
	i=0
	implication_finder = True

	while implication_finder:
		implication_finder = False
		i = 0	
		del stack[:]
		while i<len(formula):
			if formula[i]=='(':
				if (i-1)>-1:
					if formula[i-1].isalpha()==False:
						stack.append(tuple(('(', i)))
				elif i==0:
					stack.append(tuple(('(', i)))
			elif formula[i]=='=' and (i+1)<len(formula):
				if formula[i+1]=='>':
					implication_finder = True
					stack.append(tuple(('=>',i)))
			elif formula[i]==')':
				if (i-1)>-1:
					if formula[i-1].isalpha()==False:
						if stack[-1][0]=='(':
							del stack[-1]
						elif stack[-1][0]=='=>':
							for j in range(len(stack)-2,-1,-1):
								if stack[j][0]=='(':
									pos = stack[j][1]
									break
							temp_formula = formula[0:pos]
							temp_formula += remove_useless_characters('(~' + remove_useless_characters('(' + formula[pos+1:stack[-1][1]] + ')') + '|' + remove_useless_characters('(' + formula[stack[-1][1]+2:i] + ')') + ')')
							temp_formula += formula[i+1:len(formula)]
							formula = remove_useless_characters('(' + temp_formula + ')')
							m = 0
							temp_formula = list(formula)
							while(m<(len(temp_formula)-1)):
								if temp_formula[m]=='~' and temp_formula[m+1]=='~':
									temp_formula[m] = " "
									temp_formula[m+1] = " "
									m+=1
								m+=1				
							formula = remove_useless_characters('(' + "".join(temp_formula) + ')')
							break
			i+=1
	return formula

def moving_not_inwards(formula):
	not_flag = 1
	
	while not_flag!=0:
		not_flag = 0
		formula = list(formula)
		

		for i in range(len(formula)-1,-1,-1):
			if formula[i]=='~':
				if (i+1)<len(formula):
					if formula[i+1]=='(':
						not_flag = 1
						formula[i] = ' '
						brackets = 0
						j = i+1

						while(j<len(formula)):
							if formula[j]=='(':
								if (j-1)>-1:
									if formula[j-1].isalpha()==False and formula[j-1][0]!='~':
										if (j+1)<len(formula):
											if formula[j+1].isalpha()==True:
												formula[j+1] = '~' + formula[j+1]
												j += 1
											elif formula[j+1]=='~':
												formula[j+1] = ' '
												j += 2
											brackets+=1
							elif formula[j].isalpha()==True:
								if (j-1)>-1:
									if formula[j-1]=='&' or formula[j-1]=='|':
										formula[j] = '~' + formula[j]
									elif formula[j-1]=='~':
										formula[j-1] = ' '
							elif formula[j]=='&':
								formula[j] = '|'
							elif formula[j]=='|':
								formula[j] = '&'
							elif formula[j]==')':
								if (j-1)>-1:
									if formula[j-1].isalpha()==False:
										brackets -= 1

							if brackets==0:
								break
							j += 1
						formula = remove_useless_characters('(' + "".join(formula) + ')')
						break

	return "".join(formula)

def simplify(formula):
	stack = list()
	formula = list(formula)

	for i in range(0,len(formula)):
		if formula[i]=='(':
			if (i-1)>-1:
				if formula[i-1].isalpha()==False:
					stack.append(tuple(('(', i)))
			elif (i==0):
				stack.append(tuple(('(', i)))
		elif formula[i]=='&' or formula[i]=='|':
			stack.append(tuple((formula[i], i)))
		elif formula[i]==')':
			if (i-1)>-1:
				if formula[i-1].isalpha()==False:
					stack.append(tuple((')', i)))

	stack_list = [stack[i][0] for i in range(len(stack))]
	search_block = re.search(r"[\(&]\((&|(\(\|+\)))+\)[\)&]", "".join(stack_list))
	
	while(search_block!=None):
		formula[stack[search_block.start()+1][1]] = ''
		formula[stack[search_block.end()-2][1]] = ''
		del stack[search_block.end()-2]
		del stack[search_block.start()+1]
		del stack_list[search_block.end()-2]
		del stack_list[search_block.start()+1]
		search_block = re.search(r"[\(&]\((&|(\(\|+\)))+\)[\)&]", "".join(stack_list))

	stack_list = [stack[i][0] for i in range(len(stack))]
	search_block = re.search(r"[\(\|]\(\|+\)[\)\|]", "".join(stack_list))
	
	while(search_block!=None):
		formula[stack[search_block.start()+1][1]] = ''
		formula[stack[search_block.end()-2][1]] = ''
		del stack[search_block.end()-2]
		del stack[search_block.start()+1]
		del stack_list[search_block.end()-2]
		del stack_list[search_block.start()+1]
		search_block = re.search(r"[\(\|]\(\|+\)[\)\|]", "".join(stack_list))

	return remove_useless_characters('(' + "".join(formula) + ')')

def distributeOrOverAnd(formula):
	stack = list()
	formula = list(formula)

	for i in range(0,len(formula)):
		if formula[i]=='(':
			if (i-1)>-1:
				if formula[i-1].isalpha()==False:
					stack.append(tuple(('(', i)))
			elif (i==0):
				stack.append(tuple(('(', i)))
		elif formula[i]=='&' or formula[i]=='|':
			stack.append(tuple((formula[i], i)))
		elif formula[i]==')':
			if (i-1)>-1:
				if formula[i-1].isalpha()==False:
					stack.append(tuple((')', i)))	

	stack_list = [stack[i][0] for i in range(len(stack))]
	search_block = re.search(r"(\((&|(\(\|+\)))*\)\|)|(\|\((&|(\(\|+\)))*\))", "".join(stack_list))

	if search_block!=None:
		if stack[search_block.start()][0]=='|':
			temp_formula = "".join(formula[:stack[search_block.start()-1][1]+1])
		else:
			temp_formula = "".join(formula[:stack[search_block.start()][1]])
		if stack[search_block.start()][0]=='|':
			distributee_part = "".join(formula[stack[search_block.start()-1][1]+1:stack[search_block.start()][1]])
		else:
			distributee_part = formula[stack[search_block.start()][1]+1:stack[search_block.end()-1][1]-1]
		distributor_part = ""
		brackets = 0
		start_flag = 1
		pos = 0
		if stack[search_block.start()][0]!='|':
			pos = stack[search_block.end()-1][1] + 1
			while brackets!=0 or start_flag==1:
				if formula[pos]=='(':
					if start_flag==1:
						start_flag = 0
					brackets+=1
				elif formula[pos]==')':
					brackets-=1
				distributor_part += formula[pos]
				pos += 1
		else:
			distributor_part = "".join(formula[stack[search_block.start()][1]+2:stack[search_block.end()-1][1]])
		if stack[search_block.start()][0]=='|':
			last_part = "".join(formula[stack[search_block.end()-1][1]+1:])
		else:
			last_part = "".join(formula[pos:])
		
		temp_formula += '('
		if stack[search_block.start()][0]!='|':
			distributee_list_of_predicates = "".join(distributee_part).strip().split('&')
			for k in range(len(distributee_list_of_predicates)):
				temp_formula += '(' + distributee_list_of_predicates[k] + '|' + distributor_part + ')'
				if k!=(len(distributee_list_of_predicates)-1):
					temp_formula += '&'
		else:
			distributor_list_of_predicates = "".join(distributor_part).strip().split('&')
			for k in range(len(distributor_list_of_predicates)):
				temp_formula += '(' + distributee_part + '|' + distributor_list_of_predicates[k] + ')'
				if k!=(len(distributor_list_of_predicates)-1):
					temp_formula += '&'
		temp_formula += ')' + last_part
		formula = distributeOrOverAnd(simplify(remove_useless_characters(temp_formula)))
			

	return "".join(formula)

def remove_redundant_terms(formula):
	if formula[0]!='(':
		return formula
	temp_formula = formula[1:len(formula)-1]
	clauses = temp_formula.split('&')

	for c in range(len(clauses)):
		if clauses[c][0]=='(':
			temp_clause = clauses[c][1:len(clauses[c])-1]
		else:
			temp_clause = clauses[c]
		terms = temp_clause.split('|')
		i = 0
		while i<len(terms):
			if terms[i] in terms[i+1:len(terms)]:
				del terms[i]
				i = -1
			i += 1
		terms.sort()
		temp_clause = '|'.join(terms)
		clauses[c] = '(' + temp_clause + ')'

	i = 0
	while i<len(clauses):
		if clauses[i] in clauses[i+1:len(clauses)]:
			del clauses[i]
			i = -1
		i += 1

	return remove_useless_characters('(' + '&'.join(clauses) + ')')

def insert_into_kb(formula):
	if formula[0]!='(':
		predicate = formula[:formula.find('(')]
		if formula not in new_kb:
			arguments = formula[formula.find('(')+1:formula.find(')')].strip().split(',')
			for k in range(len(arguments)):
				if arguments[k][0].islower():
					arguments[k] += str(len(new_kb))
			if table_based_kb.get(predicate, -1)==-1:
				table_based_kb[predicate] = list()
			table_based_kb[predicate].append([len(arguments), arguments, len(new_kb)])
			new_formula = list()
			new_formula.append(list((predicate, len(arguments), arguments)))
			new_formula.sort()
			new_kb.append(new_formula)
	elif formula[0]=='(':
		temp_formula = formula[1:len(formula)-1]
		clauses = temp_formula.split('&')
		for i in range(len(clauses)):
			if clauses[i][0]=='(':
				new_clause = clauses[i][1:len(clauses[i])-1]
			else:
				new_clause = clauses[i]

			new_formula = list()
			
			if new_clause not in new_kb:
				predicates = new_clause.split('|')
				for p in predicates:
					term = p[:p.find('(')]
					arguments = p[p.find('(')+1:p.find(')')].strip().split(',')
					for k in range(len(arguments)):
						if arguments[k][0].islower():
							arguments[k] += str(len(new_kb))
					if table_based_kb.get(term, -1)==-1:
						table_based_kb[term] = list()
					table_based_kb[term].append([len(arguments), arguments, len(new_kb)])
					new_formula.append(list((term, len(arguments), arguments)))
					new_formula.sort()
				new_kb.append(new_formula)

def unify(arg1, arg2, theta):
	if theta==False:
		return False
	elif arg1==arg2 and len(arg1)==1 and len(arg2)==1:
		return theta
	elif len(arg1)>1 and len(arg2)>1:
		return unify(arg1[1:], arg2[1:], unify(arg1[0:1], arg2[0:1], theta))
	elif arg1[0][0].islower()==True:
		return unify_var(arg1, arg2, theta)
	elif arg2[0][0].islower()==True:
		return unify_var(arg2, arg1, theta)
	else:
		return False
	
def unify_var(var, x, theta):
	if theta!=False:
		first_terms_in_theta = list()
		for item in theta:
			first_terms_in_theta.append(item[0])

		if var[0] in first_terms_in_theta:
			for i in range(len(first_terms_in_theta)):
				if theta[i][0]==var[0]:
					pos = i
					break
			temp = list()
			temp.append(theta[i][1])
			return unify(temp, x, theta)
		elif x[0] in first_terms_in_theta:
			for i in range(len(theta)):
				if theta[i][0]==x[0]:
					pos = i
					break
			temp = list()
			temp.append(theta[i][1])
			return unify(var, temp, theta)
		else:
			theta.append(list((var[0], x[0])))
			return theta
	else:
		return theta

def resolve(query):
	explored = list()
	stack = list()
	q = list()
	lookup_predicate = ""
	explored_string = ""
	temp_query = list()
	formula = list()

	query.sort()
	for i in range(len(query)):
		temp_mini_query = ""
		temp_mini_query = list()
		temp_mini_query.append(query[i][0])
		temp_mini_query.append(query[i][1])
		temp_mini_query.append(copy.copy(query[i][2]))
		temp_query.append(temp_mini_query)

	stack.append(temp_query)

	while(1):
		if len(stack)==0:
			return 'FALSE'
		else:
			q = stack[-1]
			del stack[-1]

			explored_string = ""

			for i in range(len(q)):
				explored_string += str(q[i][0]) + str(q[i][1])
				for j in range(len(q[i][2])):
					if q[i][2][j][0].islower():
						explored_string += '#VAR#'
					else:
						explored_string += q[i][2][j]
				explored_string += " "

			explored.append(explored_string)

			for i in range(len(q)):
				if q[i][0][0]=='~':
					lookup_predicate = q[i][0][1:]
				else:
					lookup_predicate = '~' + q[i][0]

				if table_based_kb.get(lookup_predicate, -1)!=-1:
					entries = table_based_kb[lookup_predicate]

					for j in range(len(entries)):
						if entries[j][0]==q[i][1]:
							if q[i][0][0]=='~':
								checker = q[i][0][1:]
							else:
								checker = q[i][0][:]

							if [checker, q[i][1], 1] in duplicate_predicates[entries[j][2]]:
								continue
							elif [checker, q[i][1], 0] in duplicate_predicates[entries[j][2]]:
								pos = duplicate_predicates[entries[j][2]].index([checker, q[i][1], 0])
								duplicate_predicates[entries[j][2]][pos][2] = 1

							theta = unify(q[i][2], entries[j][1], [])

							if theta!=False:
								
								formula = ""
								formula = list()
								
								for k in range(len(q)):
									temp_mini_query = ""
									temp_mini_query = list()
									temp_mini_query.append(q[k][0])
									temp_mini_query.append(q[k][1])
									temp_mini_query.append(copy.copy(q[k][2]))
									formula.append(temp_mini_query)

								for k in range(len(new_kb[entries[j][2]])):
									temp_mini_query = ""
									temp_mini_query = list()
									temp_mini_query.append(new_kb[entries[j][2]][k][0])
									temp_mini_query.append(new_kb[entries[j][2]][k][1])
									temp_mini_query.append(copy.copy(new_kb[entries[j][2]][k][2]))
									formula.append(temp_mini_query)

								pos1 = -1
								pos2 = -1

								for k in range(len(formula)):
									if (formula[k][0]==lookup_predicate) and (formula[k][2]==entries[j][1]):
										if pos1==-1:
											pos1=k
									elif (formula[k][0]==q[i][0]) and (formula[k][2]==q[i][2]):
										if pos2==-1:
											pos2=k
									else:
										for l in range(len(formula[k][2])):
											for m in range(len(theta)):
												if formula[k][2][l]==theta[m][0]:
													formula[k][2][l] = theta[m][1]

								if pos1>pos2:
									del formula[pos1]
									del formula[pos2]
								else:
									del formula[pos2]
									del formula[pos1]
								formula.sort()

								if len(formula)==0:
									return 'TRUE'

								for k in range(len(formula)):
									del_list = list()
									for l in range(k+1, len(formula)):
										if formula[k][0]==formula[l][0] and formula[k][1]==formula[l][1] and formula[k][2]==formula[l][2]:
											del_list.append(l)
									del_list.sort()
									for l in range(len(del_list)-1,-1,-1):
										del formula[del_list[l]]

								explored_string = ""
								for k in range(len(formula)):
									explored_string += str(formula[k][0]) + str(formula[k][1])
									for l in range(len(formula[k][2])):
										if formula[k][2][l][0].islower():
											explored_string += "#VAR#"
										else:
											explored_string += formula[k][2][l]
									explored_string += " "

								if explored_string not in explored:
									stack.append(formula)


def convertToCNF(formula):
	formula = formula.replace(' ', '')
	formula = remove_useless_characters('(' + formula + ')')
	formula = remove_implications(formula)
	formula = moving_not_inwards(formula)
	formula = simplify(formula)
	formula = distributeOrOverAnd(formula)
	formula = remove_redundant_terms(formula)
	insert_into_kb(formula)

inputFile = 'input.txt'
outputFile = 'output.txt'
file = open(outputFile, 'w')

info = list()
query_list = list()
kb = list()
answer_tup = list()

with open(inputFile) as f:
	for line in f.readlines():
		info.append(line.strip())

for i in range(1, int(info[0])+1):
	info[i] = info[i].replace(' ','')
	predicate = info[i][:info[i].find('(')]
	if predicate[0]=='~':
		predicate = predicate[1:]
	else:
		predicate = '~' + predicate
	args = info[i][info[i].find('(')+1:info[i].find(')')]
	args = args.strip().split(',')
	new_formula = list()
	new_formula.append(list((predicate, len(args), args)))
	new_formula.sort()
	query_list.append(new_formula)

for i in range(int(info[0])+2, len(info)):
	kb.append(info[i])

for i in range(0, len(kb)):
	cnf = convertToCNF(kb[i])

actual_size = len(new_kb)

for rule in new_kb:
	duplicate_predicates.append([])
	for i in range(len(rule)):
		current_predicate = rule[i][0]
		not_flag = 0
		if current_predicate[0]=='~':
			not_flag = 1
		current_num_of_args = rule[i][1]
		for j in range(i+1, len(rule)):
			if (not_flag==0 and rule[j][0][1:]==current_predicate and rule[j][1]==current_num_of_args):
				duplicate_predicates[-1].append(list((copy.copy(current_predicate), current_num_of_args, 0)))
				break
			elif (not_flag==1 and rule[j][0]==current_predicate[1:] and rule[j][1]==current_num_of_args):
				duplicate_predicates[-1].append(list((copy.copy(current_predicate[1:]), current_num_of_args, 0)))
				break

for query in query_list:
	ans = resolve(query)
	del new_kb[actual_size+1:]
	answer_tup.append(ans)
	if ans=='TRUE':
		new_rule = list()
		new_predicate = ""
		if query[0][0][0]=='~':
			new_predicate = query[0][0][1:]
		else:
			new_predicate = '~' + query[0][0]
		new_rule.append(new_predicate)
		new_rule.append(query[0][1])
		new_rule.append(copy.copy(query[0][2]))

		if table_based_kb.get(new_predicate, -1)==-1:
			table_based_kb[new_predicate] = list()
		table_based_kb[new_predicate].append(list((query[0][1], copy.copy(query[0][2]), len(new_kb))))

		new_kb.append(new_rule)
		actual_size += 1
		duplicate_predicates.append([])

for i in range(len(answer_tup)-1):
	file.write(answer_tup[i] + "\n")
file.write(answer_tup[len(answer_tup)-1])
file.close()