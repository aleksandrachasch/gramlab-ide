 /*
  * Unitex
  *
  * Copyright (C) 2001-2009 Universit� Paris-Est Marne-la-Vall�e <unitex@univ-mlv.fr>
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

package fr.umlv.unitex;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Scanner;

import javax.swing.table.AbstractTableModel;

public class StatisticsTableModelMode2 extends AbstractTableModel {

    private String[] columnNames=new String[] {"Collocate","Occurrences in corpus",
            "Occurrence in match context","z-score"};
    
    class Mode2Data {
        String match;
        int n;
        int n2;
        float z;
    }
    
    private ArrayList<Mode2Data> data=new ArrayList<Mode2Data>();
    
    
    public StatisticsTableModelMode2(File file) {
        try {
            FileInputStream stream=new FileInputStream(file);
            
            Scanner scanner=new Scanner(stream,"UTF-16");
            scanner.useDelimiter("\r\n|\t");
            while (scanner.hasNext()) {
                Mode2Data d=new Mode2Data();
                d.match=scanner.next();
                if (!scanner.hasNextInt()) {
                    throw new IOException();
                }
                d.n=scanner.nextInt();
                if (!scanner.hasNextInt()) {
                    throw new IOException();
                }
                d.n2=scanner.nextInt();
                if (!scanner.hasNextFloat()) {
                    throw new IOException();
                }
                d.z=scanner.nextFloat();
                data.add(d);
            }
            scanner.close();
            stream.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        
    }
    
    
    public int getColumnCount() {
        return columnNames.length;
    }

    public int getRowCount() {
        return data.size();
    }

    public Object getValueAt(int rowIndex, int columnIndex) {
        Mode2Data d=data.get(rowIndex);
        switch (columnIndex) {
        case 0: return d.match;
        case 1: return d.n;
        case 2: return d.n2;
        case 3: return d.z;
        default: throw new IllegalArgumentException("Invalid columnIndex: "+columnIndex);
        }
    }

    @Override
    public String getColumnName(int column) {
        return columnNames[column];
    }
    
    @Override
    public Class<?> getColumnClass(int columnIndex) {
        switch (columnIndex) {
        case 0: return String.class;
        case 1: return Integer.class;
        case 2: return Integer.class;
        case 3: return Float.class;
        default: throw new IllegalArgumentException("Invalid columnIndex: "+columnIndex);
        }
    }
}
