package javax.microedition.lcdui;

import java.util.ArrayList;

/**
 * 
 * @author Andre Nijholt
 */
public class Form extends Screen implements CommandListener {
	private static final Command EDIT_COMMAND = new Command(1, Command.SCREEN, 0);
	private ItemStateListener itemStateListener;
	
	private ArrayList<Object> items = new ArrayList<Object>();
	private int curItemIdx = 0;
	private boolean selectedItem;
	private int height;
	private int width;
	
	private TextBox editBox;
	
	public Form(String title) {
		this.title = title;
		editBox = new TextBox(null, null, 0, TextField.ANY);
		editBox.setCommandListener(this);
		editBox.addCommand(EDIT_COMMAND);
	}
	
	public Form(String title, Item[] items) {
		this.title = title;
		for (int i = 0; (items != null) && (i < items.length); i++) {
			this.items.add(items[i]);
		}
	}
	
	public int append(Image img) {
		items.add(img);
		return (items.size() - 1);
	}

	public int append(Item item) {
		items.add(item);
		return (items.size() - 1);
	}

	public int append(String str)  {
		items.add(str);
		return (items.size() - 1);
	}
	
	public void delete(int itemNum) {
		items.remove(itemNum);
	}
	
	public void deleteAll() {
		items.clear();
	}
	
	public Object get(int itemNum) {
		return items.get(itemNum);
	}
	
	public void set(int itemNum, Item item) {
		items.set(itemNum, item);
	}
	
	public void insert(int itemNum, Item item) {
		items.add(itemNum, item);
	}
	
	public int getHeight() {
		return height;
	}
	
	public int getWidth() {
		return width;
	}
		
	public int size() {
		return items.size();
	}

	public void setItemStateListener(ItemStateListener i) {
		this.itemStateListener = i;
	}

	protected void callItemStateListener() {
		Object o = items.get(curItemIdx);
		if ((itemStateListener != null) && (o instanceof Item)) {
			itemStateListener.itemStateChanged((Item) o);
		}
	}

	protected void keyPressed(int keyCode) {
		if (selectedItem && curItemIdx >= 0) {
			if ((keyCode == Screen.KEY_RIGHT) 
					|| (keyCode == Screen.KEY_LEFT) 
					|| (keyCode == Screen.KEY_ENTER)) {
				// Update currently selected item
				((Item) items.get(curItemIdx)).keyPressed(keyCode);
			} else if (keyCode == Screen.KEY_BACK) {				
				Object o = items.get(curItemIdx);
				if (o instanceof TextField) {
					// Update currently selected TextField until keyboard enter pressed
					editBox.keyPressed(keyCode);
				} else {
					// Notify item changed and leave current selection
					callItemStateListener();
					selectedItem = false;
				}
			}				
		} else {
			// Select new item
			if (keyCode == Screen.KEY_RIGHT) {
				for (int i = curItemIdx + 1; i != curItemIdx; i++) {
					// Wrap when last item checked
					if (i >= items.size()) {
						i = 0;
					}
					
					Object o = items.get(i);
					if ((o instanceof Item) && (((Item) o).isInteractive())) {
						curItemIdx = i;
						break;
					}
				}
			} else if (keyCode == Screen.KEY_LEFT) {
				for (int i = curItemIdx - 1; i != curItemIdx; i--) {
					// Wrap when first item checked
					if (i < 0) {
						i = (items.size() - 1);
					}
					
					Object o = items.get(i);
					if ((o instanceof Item) && (((Item) o).isInteractive())) {
						curItemIdx = i;
						break;
					}
				}
			} else if (keyCode == Screen.KEY_BACK) {
				for (int i = 0; i < commands.size(); i++) {
					callCommandListener();
				}
			} else if (keyCode == Screen.KEY_ENTER) {
				Object o = items.get(curItemIdx);
				if (o instanceof TextField) {
					// Show text box for editing
					editBox.setTitle(((TextField) o).getLabel());
					editBox.setText(((TextField) o).getText());
					editBox.setMaxSize(((TextField) o).getMaxSize());
					editBox.setConstraints(((TextField) o).getConstraints());
					Display.getDisplay().setCurrent(editBox);
				} else {
					// Set current selection
					selectedItem = true;
				}
			}
		}
		repaint();
	}

	public void commandAction(Command c, Displayable d) {
		if ((c == EDIT_COMMAND) && (d == editBox)) {
			// Update textfield and return to form display
			TextField tf = (TextField) items.get(curItemIdx);
			tf.setText(editBox.getText());
			callItemStateListener();
			Display.getDisplay().setCurrent(this);
		}
	}

	public void paint(Graphics g) {
		int curX = 0;
		int curY = 0;
		int curWidth;
		int curHeight;

		ChoiceGroup activePopup = null;
		int popupX = 0;
		int popupY = 0;

		// Draw title on entire line
		if (title != null) {
			g.drawString(title, 0, 0, 0);
			curY += Display.CHAR_HEIGHT;
		}
		
		// Draw all items
		for (int i = 0; i < items.size(); i++) {
			// Calculate position and size for current item
			curWidth = getItemWidth(i);
			curHeight = getItemHeight(i);
			if (((curX + curWidth) < Display.SCREEN_WIDTH)
					&& ((curX + curWidth + getItemWidth(i + 1)) > Display.SCREEN_WIDTH)) {
				// Next item doesn't fit on current line: allow entire line for current item
				curWidth = Display.SCREEN_WIDTH - curX;
			}

			// Draw current item
			Object o = items.get(i);
			if (o instanceof Image) {
				g.drawImage((Image) o, curX, curY, 0);
			} else if (o instanceof String) {
				g.drawString(((String) o), curX, curY, 0);
			} else if (o instanceof Item) {
				((Item) o).paint(g, curX, curY, curWidth, curHeight, (i == curItemIdx));
				
				if (selectedItem && (i == curItemIdx) && (o instanceof ChoiceGroup)
						&& (((ChoiceGroup) o).choiceType == Choice.POPUP)) {
					// Draw popup window again after all items drawn
					activePopup = (ChoiceGroup) o;
					popupX = curX;
					popupY = curY;
				}
			}
			
			if ((curX + curWidth) < Display.SCREEN_WIDTH) {
				// Draw next item on current line
				curX += curWidth;
				if (curHeight > Display.CHAR_HEIGHT) {
					curY += (curHeight - Display.CHAR_HEIGHT);
				}
			} else {
				// Start new line and draw item
				curX = 0;
				curY += curHeight;
			}
		}
		
		// Draw popup menu above currently drawn items
		if (activePopup != null) {
			int popupHeight = ((activePopup.label != null) ? Display.CHAR_HEIGHT : 0)
				+ (activePopup.size() * Display.CHAR_HEIGHT);
			if ((popupY + popupHeight) > Display.SCREEN_HEIGHT) {
				popupHeight = Display.SCREEN_HEIGHT - popupY;
			}
			activePopup.paint(g, popupX, popupY, activePopup.getMinimumWidth(), 
					popupHeight, true);
		}
	}
	
	private int getItemWidth(int itemIdx) {
		if (itemIdx >= items.size()) {
			return 0;
		}
		
		Object o = items.get(itemIdx);
		if (o instanceof Image) {
			return ((Image) o).getWidth();
		} else if (o instanceof String) {
			return ((String) o).length() * Display.CHAR_WIDTH;
		} else if (o instanceof Item) {
			return Math.max(((Item) o).getMinimumWidth(), ((Item) o).getPreferredWidth());
		}

		return 0;
	}

	private int getItemHeight(int itemIdx) {
		if (itemIdx >= items.size()) {
			return 0;
		}
		
		Object o = items.get(itemIdx);
		int height = 0;
		if (o instanceof Image) {
			height = ((Image) o).getHeight();
		} else if (o instanceof String) {
			// Always single line string
			height = Display.CHAR_HEIGHT;
		} else if (o instanceof Item) {
			height = Math.max(((Item) o).getMinimumHeight(), ((Item) o).getPreferredHeight());
		}

		// Round to multiple of line height
		height = (((height + Display.CHAR_HEIGHT - 1) / Display.CHAR_HEIGHT)) * Display.CHAR_HEIGHT;
		return height;
	}
}
