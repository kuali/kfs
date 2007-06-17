package org.kuali.module.pdp.xml.impl;

import java.util.EmptyStackException;
import java.util.Iterator;
import java.util.Stack;

public class TagStack {
  private Stack tags = new Stack();

  public TagStack() {
    super();
  }

  public void push(String name) {
    tags.add(name);
  }

  public void pop() throws EmptyStackException {
    tags.pop();
  }

  public String peek() throws EmptyStackException {
    return (String)tags.peek();
  }

  public boolean empty() {
    return (tags.size() == 0);
  }

  public String getTagPath() {
    StringBuffer name = new StringBuffer();
    Iterator i = tags.iterator();
    while ( i.hasNext() ) {
      if ( name.length() > 0 ) {
        name.append("/");
      }
      name.append(i.next());
    }
    return name.toString();
  }
}
