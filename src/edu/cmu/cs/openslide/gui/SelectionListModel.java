/*
 *  OpenSlide, a library for reading whole slide image files
 *
 *  Copyright (c) 2007-2010 Carnegie Mellon University
 *  All rights reserved.
 *
 *  OpenSlide is free software: you can redistribute it and/or modify
 *  it under the terms of the GNU Lesser General Public License as
 *  published by the Free Software Foundation, version 2.1.
 *
 *  OpenSlide is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 *  GNU Lesser General Public License for more details.
 *
 *  You should have received a copy of the GNU Lesser General Public
 *  License along with OpenSlide. If not, see
 *  <http://www.gnu.org/licenses/>.
 *
 */

package edu.cmu.cs.openslide.gui;

import java.awt.Shape;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import javax.swing.AbstractListModel;

public class SelectionListModel extends AbstractListModel implements
        Iterable<Shape> {

    private final List<Shape> list = new ArrayList<Shape>();

    @Override
    public Shape getElementAt(int index) {
        return list.get(index);
    }

    @Override
    public int getSize() {
        return list.size();
    }

    public void add(Shape s) {
        list.add(s);
        int i = list.size() - 1;
        fireIntervalAdded(this, i, i);
    }

    public void clear() {
        int oldSize = list.size();
        list.clear();
        fireIntervalRemoved(this, 0, oldSize - 1);
    }

    public boolean isEmpty() {
        return list.isEmpty();
    }

    public Shape get(int index) {
        return list.get(index);
    }

    @Override
    public Iterator<Shape> iterator() {
        return list.iterator();
    }
}