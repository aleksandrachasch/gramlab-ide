/*
 * Unitex
 *
 * Copyright (C) 2001-2016 Université Paris-Est Marne-la-Vallée <unitex@univ-mlv.fr>
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 2.1 of the License, or (at your option) any later version.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 * 
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307  USA.
 *
 */
package fr.umlv.unitex.process.commands;

import java.io.File;

/**
 * @author Sébastien Paumier
 * 
 */
public class SortTxtCommand extends CommandBuilder {
	public SortTxtCommand() {
		super("SortTxt");
	}

	public SortTxtCommand file(File s) {
		protectElement(s.getAbsolutePath());
		return this;
	}

	public SortTxtCommand removeDuplicates(boolean remove) {
		element(remove ? "-n" : "-d");
		return this;
	}

	public SortTxtCommand reverse() {
		element("-r");
		return this;
	}

	public SortTxtCommand sortAlphabet(File alphabet) {
		if (alphabet != null) {
			protectElement("-o" + alphabet.getAbsolutePath());
		}
		return this;
	}

	public SortTxtCommand saveNumberOfLines(File file) {
		protectElement("-l" + file.getAbsolutePath());
		return this;
	}

	public SortTxtCommand thai(boolean b) {
		if (b) {
			element("--thai");
		}
		return this;
	}

	public SortTxtCommand factorizeInflectionalCodes() {
		element("-f");
		return this;
	}
}
