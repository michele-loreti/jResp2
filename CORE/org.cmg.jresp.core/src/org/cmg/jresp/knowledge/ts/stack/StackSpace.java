/**
 * Copyright (c) 2012 Concurrency and Mobility Group.
 * Universita' di Firenze
 *	
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *      Michele Loreti
 *      Alberto Lluch Lafuente
 */
package org.cmg.jresp.knowledge.ts.stack;

import java.util.Iterator;
import java.util.LinkedList;
import java.util.ListIterator;

import org.cmg.jresp.knowledge.KnowledgeManager;
import org.cmg.jresp.knowledge.Template;
import org.cmg.jresp.knowledge.Tuple;

/**
 * This class provides a LIFO/Stack implementation of {@link KnowledgeManager}.
 * get/query consider the newest inserted tuple/message
 * getAll provides the longest upper part of the stackthe pattern 
 * 
 * @author Alberto Lluch Lafuente
 *
 */
public class StackSpace implements KnowledgeManager {

	/**
	 * Elements in the tuple space are arranged in a list.
	 */
	LinkedList<Tuple> elements;

	public StackSpace() {
		elements = new LinkedList<Tuple>();
	}

	/*
	 * Add tuple t to the tuple space.
	 * 
	 * @see org.cmg.scel.knowledge.Knowledge#put(org.cmg.scel.knowledge.Tuple)
	 */
	@Override
	public synchronized boolean put(Tuple t) {
		boolean result = elements.add(t);
		notifyAll();
		return result;
	}

	/*
	 * Removes the first tuple matching template from the tuple space.
	 * 
	 * @return retrieved tuple
	 * 
	 * @see
	 * org.cmg.scel.knowledge.Knowledge#get(org.cmg.scel.knowledge.Template)
	 */
	@Override
	public Tuple get(Template template) throws InterruptedException {
		return _InRead(template, true);
	}

	private synchronized Tuple _InRead(Template template, boolean isIn) throws InterruptedException {
		Tuple t;
		while (((t = _get(template, isIn)) == null)) {
			wait();
		}
		return t;
	}

	private Tuple _get(Template template, boolean remove) {
		
		if (elements.isEmpty()) return null;
		
		Tuple t = elements.getLast();
		if (template.match(t)) {
			if (remove) {
				elements.pollLast();
			}
			return t;
		}

		return null;
	}

	private synchronized Tuple _InReadP(Template template, boolean remove) {
		return _get(template, remove);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.cmg.scel.knowledge.Knowledge#getp(org.cmg.scel.knowledge.Template)
	 */
	@Override
	public Tuple getp(Template template) {
		return _InReadP(template, true);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.cmg.scel.knowledge.Knowledge#getAll(org.cmg.scel.knowledge.Template)
	 */
	@Override
	public LinkedList<Tuple> getAll(Template template) {
		return _InReadAll(template, true);
	}

	/**
	 * @param template
	 * @param b
	 * @return
	 */
	private synchronized LinkedList<Tuple> _InReadAll(Template template, boolean remove) {
		LinkedList<Tuple> toReturn = new LinkedList<Tuple>();
		Tuple t;
		ListIterator<Tuple> itr = elements.listIterator(elements.size());
		
		while(itr.hasPrevious()) {
			t = itr.previous();
			if (template.match(t)) {
				toReturn.add(t);
				if (remove) {
					// According to the documentation this is the only way
					// of deleting while iterating.
					itr.remove();
				}
			} else {
				// break as soon as a message does not match the template
				break;
			}
		}
		return toReturn;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.cmg.scel.knowledge.Knowledge#query(org.cmg.scel.knowledge.Template)
	 */
	@Override
	public Tuple query(Template template) throws InterruptedException {
		return _InRead(template, false);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.cmg.scel.knowledge.Knowledge#queryp(org.cmg.scel.knowledge.Template)
	 */
	@Override
	public Tuple queryp(Template template) {
		return _InReadP(template, false);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.cmg.scel.knowledge.Knowledge#queryAll(org.cmg.scel.knowledge.
	 * Template)
	 */
	@Override
	public LinkedList<Tuple> queryAll(Template template) {
		return _InReadAll(template, false);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.cmg.scel.knowledge.Knowledge#getKnowledgeItems()
	 */
	@Override
	public Tuple[] getKnowledgeItems() {
		return elements.toArray(new Tuple[elements.size()]);
	}

}
