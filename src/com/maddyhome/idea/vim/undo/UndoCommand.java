package com.maddyhome.idea.vim.undo;

/*
* IdeaVim - A Vim emulator plugin for IntelliJ Idea
* Copyright (C) 2003 Rick Maddy
*
* This program is free software; you can redistribute it and/or
* modify it under the terms of the GNU General Public License
* as published by the Free Software Foundation; either version 2
* of the License, or (at your option) any later version.
*
* This program is distributed in the hope that it will be useful,
* but WITHOUT ANY WARRANTY; without even the implied warranty of
* MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
* GNU General Public License for more details.
*
* You should have received a copy of the GNU General Public License
* along with this program; if not, write to the Free Software
* Foundation, Inc., 59 Temple Place - Suite 330, Boston, MA  02111-1307, USA.
*/

import com.intellij.openapi.actionSystem.DataContext;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.diagnostic.Logger;
import com.maddyhome.idea.vim.group.MotionGroup;
import java.util.ArrayList;

/**
 *
 */
public class UndoCommand
{
    public UndoCommand(Editor editor)
    {
        this.editor = editor;
        startOffset = editor.getCaretModel().getOffset();
        logger.debug("new undo command: startOffset=" + startOffset);
    }

    public void complete()
    {
        endOffset = editor.getCaretModel().getOffset();
    }

    public void addChange(DocumentChange change)
    {
        logger.debug("new change");
        if (changes.size() == 0)
        {
//            startOffset = editor.getCaretModel().getOffset();
            logger.debug("startOffest=" + startOffset);
        }

        changes.add(change);
    }

    public void redo(Editor editor, DataContext context)
    {
        for (int i = 0; i < changes.size(); i++)
        {
            DocumentChange change = (DocumentChange)changes.get(i);
            change.redo(editor, context);
        }

        //editor.getCaretModel().moveToOffset(endOffset);
        MotionGroup.moveCaret(editor, context, endOffset);
    }

    public void undo(Editor editor, DataContext context)
    {
        logger.debug("undo: startOffset=" + startOffset);
        for (int i = changes.size() - 1; i >= 0; i--)
        {
            DocumentChange change = (DocumentChange)changes.get(i);
            change.undo(editor, context);
        }

        //editor.getCaretModel().moveToOffset(startOffset);
        MotionGroup.moveCaret(editor, context, startOffset);
    }

    public String toString()
    {
        StringBuffer res = new StringBuffer();
        res.append("UndoCommand[");
        res.append("startOffset=" + startOffset);
        res.append(", endOffset=" + endOffset);
        res.append(", changes=" + changes);
        res.append("]");

        return res.toString();
    }

    public int size()
    {
        return changes.size();
    }

    private Editor editor;
    private int startOffset;
    private int endOffset;
    private ArrayList changes = new ArrayList();

    private static Logger logger = Logger.getInstance(UndoCommand.class.getName());
}
