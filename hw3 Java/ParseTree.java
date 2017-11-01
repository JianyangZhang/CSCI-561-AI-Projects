class ParseTree {
public String value;
public ParseTree root;
public ParseTree left;
public ParseTree right;
public ParseTree parent;
ParseTree (Object object) {
    this.value = (String) object;
    right = null;
    left = null;
    parent = null;
}

public ParseTree() {
	// TODO Auto-generated constructor stub
	right = null;
    left = null;
    parent = null;
    value="";
}

public void setKey(String value) {
    this.value = value;
}

public String getKey() {
    return this.value;
}

public void setLeft(ParseTree left) {
    this.left = left;
}

public ParseTree getLeft() {
    return this.left;
}
public ParseTree getParent() {
    return this.parent;
}

public void setParent(ParseTree parent ) {
    this.parent = parent;
}

public void setRight(ParseTree right ) {
    this.right = right;
}

public ParseTree getRight() {
    return right;
}

}