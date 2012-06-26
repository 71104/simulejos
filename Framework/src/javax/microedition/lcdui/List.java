package javax.microedition.lcdui;

import java.util.ArrayList;

/**
 * 
 * @author Andre Nijholt
 */
public class List extends Screen implements Choice {
	/** Default command for implicit lists */
	private final Command SELECT_COMMAND = new Command(0, Command.SCREEN, 0);

	protected int listType;
	protected ArrayList<ListItem> listItems;
//	private int fitPolicy;
	
	/** Scrolling administration */
	private int scrollFirst = 0;
	private int scrollCurr 	= 0;
	private int scrollLast 	= 0;
	private boolean scrollWrap = true;
	
	public List(String title, int listType) {
		if (listType == Choice.POPUP) {
			// Type POPUP not allowed
			throw new IllegalArgumentException();
		}
		this.title = title;
		this.listType = listType;
		listItems = new ArrayList<ListItem>();
	}
	
	public List(String title, int listType, String[] stringElements, Image[] imageElements) {
		if (listType == Choice.POPUP) {
			// Type POPUP not allowed
			throw new IllegalArgumentException();
		}
		this.title = title;
		this.listType = listType;

		listItems = new ArrayList<ListItem>(stringElements.length);
		for (int i = 0; i < stringElements.length; i++) {
			listItems.add(new ListItem(stringElements[i], imageElements[i]));
			scrollLast++;
		}
	}

	public int append(String stringPart, Image imagePart) {
		listItems.add(new ListItem(stringPart, imagePart));
		scrollLast++;
		return listItems.size();
	}
	
	public void delete(int elementNum) {
		scrollLast--;
		listItems.remove(elementNum);
	}
	
	public void deleteAll() {
		scrollLast = 0;
		listItems.clear();
	}

// TODO: FitPolicy currently not supported: no wrapping allowed
//	public int getFitPolicy() {
//		return fitPolicy;
//	}
//	
//    public void setFitPolicy(int fitPolicy) {
//    	this.fitPolicy = fitPolicy;
//    }

// TODO: Multiple fonts currently not supported	
//	public Font getFont(int elementNum) {
//		return ((ListItem) listItems.get(elementNum)).font;
//	}
//	
//    public void setFont(int elementNum, Font font) {
//    	((ListItem) listItems.get(elementNum)).font = font;
//    } 

	public Image getImage(int elementNum) {
		return (listItems.get(elementNum)).img;
	}

	public int getSelectedFlags(boolean[] selectedArray_return) {
		selectedArray_return = new boolean[listItems.size()];
		for (int i = 0; i < selectedArray_return.length; i++) {
			selectedArray_return[i] = listItems.get(i).selected;
		}
		
		return selectedArray_return.length;
	}

	public int getSelectedIndex() {
		for (int i = 0; i < listItems.size(); i++) {
			if (listItems.get(i).selected) {
				return i;
			}
		}

		return -1;
	}
	public String getString(int elementNum) {
		return listItems.get(elementNum).str;
	} 
	
	public void insert(int elementNum, String stringPart, Image imagePart) {
		listItems.add(elementNum, new ListItem(stringPart, imagePart));
	}
	
	public boolean isSelected(int elementNum) {
		return listItems.get(elementNum).selected;
	}

	public void set(int elementNum, String stringPart, Image imagePart) {
		listItems.set(elementNum, new ListItem(stringPart, imagePart));
	}
	
    public void setScrollWrap(boolean scrollWrap) {
    	this.scrollWrap = scrollWrap;
    }

    public void setSelectedFlags(boolean[] selectedArray) {
		for (int i = 0; i < listItems.size(); i++) {
			listItems.get(i).selected = selectedArray[i];
		}
    } 

    public void setSelectedIndex(int elementNum, boolean selected) {
    	if ((listType == Choice.MULTIPLE) || !selected) {
			// Just set/clear selection
			listItems.get(elementNum).selected = selected;    		
    	} else {
			// Set single selection for these types
			for (int i = 0; i < listItems.size(); i++) {
				ListItem li = listItems.get(i);
				li.selected = (i == elementNum);
			}
		} 
    }
    
    public int size() {
    	return listItems.size();
    }
    
	protected void keyPressed(int keyCode) {
		if (keyCode == KEY_RIGHT) {
			if (scrollWrap) {
				scrollCurr = (scrollCurr + 1) % listItems.size();
			} else if (scrollCurr < (listItems.size() - 1)) {
				scrollCurr++;
			}
			repaint();
		} else if (keyCode == KEY_LEFT) {
			if (scrollWrap) {
				scrollCurr = (scrollCurr == 0) 
					? (listItems.size() - 1) : (scrollCurr - 1);
			} else if (scrollCurr > 0) {
				scrollCurr--;
			}
			repaint();
		} else if (keyCode == KEY_BACK) {
			callCommandListener();
		} else if (keyCode == KEY_ENTER) {
			ListItem li = listItems.get(scrollCurr);
			if (listType == Choice.IMPLICIT) 
				setSelectedIndex(scrollCurr, false); 
			setSelectedIndex(scrollCurr, !li.selected);

//			if ((listType == Choice.IMPLICIT) || (listType == Choice.EXCLUSIVE)) {
//				// Set single selection for these types
//				for (int i = 0; i < listItems.size(); i++) {
//					if ((scrollCurr == i)) {
//						// Toggle selection (discard current state when IMPLICIT)
//						ListItem li = listItems.get(scrollCurr);
//						setSelectedIndex(scrollCurr, (listType == Choice.IMPLICIT)
//								? true : !li.selected);						
//					} else {
//						// Multiple items cannot be selected for this listType
//						setSelectedIndex(i, false);
//					}
//				}
//			} else {
//				// Toggle selection
//				ListItem li = listItems.get(scrollCurr);
//				setSelectedIndex(scrollCurr, !li.selected);
//			}
			
			// Send selection command for implicit list only
			if ((listType == Choice.IMPLICIT) && (cmdListener != null)) {
				cmdListener.commandAction(SELECT_COMMAND, this);
			}	
			repaint();
		}
	}
    
    protected void paint(Graphics g) {
    	int lineIdx = 0;
		int line = Display.CHAR_HEIGHT;
		int ch = Display.CHAR_WIDTH;
    	if (ticker != null) {
    		lineIdx++;
    	}
    	if (title != null) {
    		g.drawString(title, 0, lineIdx++ * line, 0);
    	}

    	// Update scrolling administration
    	int scrollLines = Display.SCREEN_CHAR_DEPTH - lineIdx;
    	if (scrollCurr == 0) {
    		scrollFirst = 0;
    		scrollLast = scrollLines;
    	} else if ((listItems.size() >= scrollLines) 
    			&& (scrollCurr >= (listItems.size() - 1))) {
    		scrollFirst = listItems.size() - scrollLines;
    		scrollLast = listItems.size() - 1;
    	} else if (scrollCurr >= scrollLast) {
    		scrollFirst++;
    		scrollLast++;
    	} else if (scrollCurr < scrollFirst) {
    		scrollFirst--;
    		scrollLast--;
    	}

    	// Display list items with current highlighted
		for (int i = scrollFirst; (i < listItems.size()) && (i <= scrollLast); i++) {
			ListItem li = listItems.get(i);
			g.drawString(li.str, 2*ch, lineIdx*line, 0, (i == scrollCurr));
			
			// Draw selection state
			if ((listType == Choice.EXCLUSIVE) || (listType == Choice.MULTIPLE)) {
				if (li.selected) {
					g.fillArc(2, lineIdx * 8, 8, 8, 0, 360);
				} else {
					g.drawArc(2, lineIdx * 8, 8, 8, 0, 360);
				}
			}
			
			lineIdx++;
		}
    }
    
    private class ListItem {
    	String str;
    	Image img;
    	boolean selected;
//    	Font font;

    	ListItem(String stringPart, Image imagePart) {
    		this.str = stringPart;
    		this.img = imagePart;
    		this.selected = false;
//    		this.font = null;
    	}    	
    }
}
