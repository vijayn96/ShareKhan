package com.snapwork.components;

import net.rim.device.api.system.Bitmap;
import net.rim.device.api.ui.Field;
import net.rim.device.api.ui.Graphics;
import net.rim.device.api.ui.Manager;
import com.snapwork.util.AppConstants;
import com.snapwork.util.FontLoader;
import com.snapwork.util.ImageManager;

/**
 * 
 * <p>This class creates Title bar layout like adding background image and text at title.
 * <p>All View Screens used this class for showing title bar.
 *
 */
public class TitleBarRefreshWL extends Manager
{
	private static Bitmap bmpTitleBarBg = null;
	private static int width = 0,height = 0;
	public static byte padding = 5;
	
	public TitleBarRefreshWL(String strTitleText, final Field edit ,final RefreshButton refresh) {
		super(NON_FOCUSABLE);

		if(bmpTitleBarBg==null) {
			bmpTitleBarBg = ImageManager.getTitleBarImage();
			
			width = bmpTitleBarBg.getWidth();
			height = bmpTitleBarBg.getHeight();
		}

		CustomLabelField lblTitleText = new CustomLabelField(strTitleText, 0, 0xeeeeee, FontLoader.getFont(AppConstants.BIG_PLAIN_FONT));
		//System.out.println(strTitleText);
		if(strTitleText.indexOf("&amp;")>-1)
			strTitleText = strTitleText.substring(0,strTitleText.indexOf("&amp;")+1)+strTitleText.substring(strTitleText.indexOf("&amp;")+5,strTitleText.length());
		
		if(strTitleText.length()>22)
			strTitleText = strTitleText.substring(0, 19)+"...";
		//System.out.println(strTitleText);
		//System.out.println(lblTitleText.getText());
		lblTitleText.setText(strTitleText);
		add(edit);
		add(lblTitleText);
		/*ButtonField edit = new ButtonField(secondtxt, FOCUSABLE | FIELD_RIGHT | USE_ALL_HEIGHT)
		{
				public net.rim.device.api.ui.Font getFont() {
				return FontLoader.getFont(AppConstants.SMALL_PLAIN_FONT);
			}
			protected boolean navigationClick(int arg0, int arg1) {
				if(secondtxt.equalsIgnoreCase("Edit")){
				Vector vectorCommandData = new Vector();
				vectorCommandData.addElement(Utils.WATCHLIST_LABEL);
				Utils.WATCHLIST = false;
				Action action = new Action(ActionCommand.CMD_WL_EDIT_SCREEN, vectorCommandData);
				ActionInvoker.processCommand(action);
				}
				return super.navigationClick(arg0, arg1);
			}
		};*/
			add(refresh);
		//add(AdsManager.getAdsManager(Manager.FIELD_RIGHT | Manager.NO_HORIZONTAL_SCROLL | Manager.NO_HORIZONTAL_SCROLLBAR));
	}

	public static int getItemHeight() {
		return height;
	}
	
	public int getPreferredHeight() {
		return height;
	}

	public int getPreferredWidth() {
		return width;
	}
	
	public static int getStringWidth(String text) {
		return FontLoader.getFont(AppConstants.BIG_BOLD_FONT).getAdvance(text);
	}
	
    protected void paintBackground(Graphics graphics) {
		if(bmpTitleBarBg!=null) {
			graphics.drawBitmap(0, 0, bmpTitleBarBg.getWidth(), bmpTitleBarBg.getHeight(), bmpTitleBarBg, 0, 0);
		}
	}
	
	protected void sublayout(int width, int height) {
		
		
		//if(AppConstants.screenWidth==480 && AppConstants.screenHeight==640 || AppConstants.screenHeight==800 && AppConstants.screenWidth==480)
		if(AppConstants.screenHeight>240)
		{
			layoutChild(this.getField(0), this.getField(0).getPreferredWidth()+20, height - 20);
			setPositionChild(this.getField(0), padding, 0);
			layoutChild(this.getField(1), width, height-20);
			setPositionChild(this.getField(1), padding+padding+20+this.getField(0).getPreferredWidth(), getPreferredHeight()/2 - this.getField(1).getFont().getHeight()/2);
			layoutChild(this.getField(2), this.getField(2).getPreferredWidth(), this.getField(2).getPreferredHeight());
			
			if(AppConstants.screenHeight==480)
				setPositionChild(this.getField(2), getPreferredWidth() - this.getField(2).getPreferredWidth() - 10, 3);
			else
				setPositionChild(this.getField(2), getPreferredWidth() - this.getField(2).getPreferredWidth() - 10, (getPreferredHeight()/2) - (this.getField(2).getPreferredHeight()/2) - 1);
		}
		else
			{
			layoutChild(this.getField(0), this.getField(0).getPreferredWidth()+20, height - 8);
			setPositionChild(this.getField(0), padding, 0);
			layoutChild(this.getField(1), width, height-8);
			setPositionChild(this.getField(1), padding+padding+20+this.getField(0).getPreferredWidth(), getPreferredHeight()/2 - this.getField(1).getFont().getHeight()/2);
			layoutChild(this.getField(2), this.getField(2).getPreferredWidth(), this.getField(2).getPreferredHeight());
			setPositionChild(this.getField(2), getPreferredWidth() - this.getField(2).getPreferredWidth() - 10, (getPreferredHeight()/2) - (this.getField(2).getPreferredHeight()/2) - 1);
			}
		setExtent(getPreferredWidth(),getPreferredHeight());
		//setExtent(Math.min(width, getPreferredWidth()),Math.min(height, getPreferredHeight()));
	}

}
