package com.snapwork.view.trade;

import java.util.Hashtable;
import java.util.Vector;
import net.rim.device.api.system.Bitmap;
import net.rim.device.api.ui.Color;
import net.rim.device.api.ui.DrawStyle;
import net.rim.device.api.ui.Font;
import net.rim.device.api.ui.Graphics;
import net.rim.device.api.ui.Manager;
import net.rim.device.api.ui.TouchEvent;
import net.rim.device.api.ui.UiApplication;
import net.rim.device.api.ui.component.BitmapField;
import net.rim.device.api.ui.component.Dialog;
import net.rim.device.api.ui.component.LabelField;
import net.rim.device.api.ui.component.NullField;
import net.rim.device.api.ui.container.VerticalFieldManager;

import com.snapwork.actions.Action;
import com.snapwork.actions.ActionCommand;
import com.snapwork.actions.ActionInvoker;
import com.snapwork.actions.ActionListener;
import com.snapwork.beans.HomeJson;
import com.snapwork.interfaces.ReturnDataWithId;
import com.snapwork.interfaces.ReturnString;
import com.snapwork.parsers.HomeJsonParser;
import com.snapwork.parsers.ReturnStringParser;
import com.snapwork.parsers.TradeNowMainParser;
import com.snapwork.util.AppConstants;
import com.snapwork.util.FontLoader;
import com.snapwork.util.ImageManager;
import com.snapwork.util.LOG;
import com.snapwork.util.ScreenInvoker;
import com.snapwork.util.URLEncode;
import com.snapwork.util.UserInfo;
import com.snapwork.util.Utils;

public class ReportsNetPositionView extends VerticalFieldManager implements ReturnString, ReturnDataWithId {

	private static byte padding = 4; 
	private final Bitmap bmp = ImageManager.getSmallTradeButton();
	private ActionListener actionListener;
	final ReturnDataWithId returnDataWithId = this;
	final ReturnString returnString = this;
	private HomeJson bannerData;
	private Hashtable hashData;
	private String url;
	private long timer;

	public ReportsNetPositionView(String url,Hashtable dataHolder,ActionListener actionListener, int id) {
		this.url = url;
		timer = System.currentTimeMillis();
		ReportsOrderGroup tlv = null;
		this.actionListener = actionListener;
		this.hashData = dataHolder;
		tlv = new ReportsOrderGroup();
		tlv.addItem(dataHolder, id);
		add(tlv);
	}

	public int getPreferredHeight() {
		try {
			return AppConstants.screenHeight;
		} catch(Exception ex) {
		}
		return 0;
	}

	private class ReportsOrderGroup extends VerticalFieldManager {
		public ReportsOrderGroup() {
			super(FOCUSABLE);
		}

		private void addItem(final Hashtable dataHolder, final int id) {
			UiApplication.getUiApplication().invokeLater(new Runnable() {

				public void run() {
					add(new ReportsOrderGroupItem(FOCUSABLE, dataHolder, id));
				}
			});
		}
	}

	private class ReportsOrderGroupItem extends Manager {

		private boolean requireNullField = true;
		private int id ;
		public ReportsOrderGroupItem(long style,Hashtable dataHolder,int id) {
			super(style);
			this.id = id;
			if(id == 0)
			{
				add(getLabel((String) dataHolder.get("Scrip-Exchange"), FontLoader.getFont(AppConstants.REPORTSMARGIN_FONT), 0xeeeeee, 0x6c8889));
				add(getLabel((String) dataHolder.get("NetQty"), FontLoader.getFont(AppConstants.REPORTSMARGIN_FONT), 0xeeeeee, 0x6c8889));
				add(getSmallBitmapFieldBlank("Details",0x6c8889));
				add(getSmallBitmapFieldBlank("Square Off",0x6c8889));
				//add(new NullField(NON_FOCUSABLE));
			}
			else
			{
				String text = "";
				if((String) dataHolder.get("SCRIPCODE") == null)
					text = " - ";
				else
					text = (String) dataHolder.get("SCRIPCODE") + " - ";
				if((String) dataHolder.get("EXCHANGE") == null);
				else
					text = text + (String) dataHolder.get("EXCHANGE");
				add(getLabel(text, FontLoader.getFont(AppConstants.REPORTSMARGIN_FONT), 0xeeeeee, 0x6c8889));
				text = "";
				if((String) dataHolder.get("NETQTY") == null)
					text = " - ";
				else
					text = (String) dataHolder.get("NETQTY");
				add(getLabel(text, FontLoader.getFont(AppConstants.REPORTSMARGIN_FONT), 0xeeeeee, 0x6c8889));
				dataHolder.put("url", url);
				add(getSmallBitmapField("Details", dataHolder));
				add(getSmallBitmapField("Square Off", dataHolder));
				//add(new NullField(NON_FOCUSABLE));
			} requireNullField = true;
			for(int i=0;i<this.getFieldCount();i++) {
				if(this.getField(i).isFocusable()) {
					requireNullField = false;
					i=this.getFieldCount();
				}
			}

			if(requireNullField) {
				NullField objNullField = new NullField(FOCUSABLE) {

					protected void onFocus(int direction) {
						if(direction==-1) {
							this.setPosition(0, 0);
						}
						else {
							this.setPosition(this.getManager().getWidth(), this.getManager().getHeight());
						}
						super.onFocus(direction);
					}

					protected void onUnfocus() {
						super.onUnfocus();
					}
				};
				add(objNullField);//A Field which will show focus on manager
			}

		}

		public int getTotalFields() {
			if(requireNullField)
				return this.getFieldCount()-1;
			else
				return this.getFieldCount(); 
		}

		public int getPreferredHeight() {
			return getFont().getHeight()+(getFont().getHeight()/2)+ 4;
		}

		public int getPreferredWidth() {
			return AppConstants.screenWidth;
		}

		protected void onFocus(int direction) {
			super.onFocus(direction);
			invalidate();
		}

		protected void onUnfocus() {
			super.onUnfocus();
			invalidate();
		}

		protected boolean navigationClick(int status, int time) {
			return super.navigationClick(status, time);
		}

		protected void paintBackground(Graphics graphics) {
			if(id == 0)
			{
				// Header draw;
				graphics.setColor(0x000000);//0x96abab);
				graphics.fillRect(0, 0, getPreferredWidth(), getPreferredHeight());
				graphics.setColor(0x6c8889);
				graphics.fillRect(2, 2, getPreferredWidth()-4, getPreferredHeight()-4);
			}
			else
			{
				graphics.setColor(0x000000);//0x96abab);
				graphics.fillRect(0, 0, getPreferredWidth(), getPreferredHeight());
				int color;
				int bgcolor;
				if(id%2!=0) {
					color = 0x000000;
					bgcolor = 0x333333;
				}
				else {
					color = 0x333333;
					bgcolor = 0x000000;
				}
				graphics.setColor(bgcolor);
				graphics.fillRect(2, 2, getPreferredWidth()-4, getPreferredHeight()-4);
				if(isFocus())
					graphics.setColor(Color.ORANGE);
				else
					graphics.setColor(color);
				graphics.drawRect(2, 2, getPreferredWidth()-4, getPreferredHeight()-4);

			}
		}

		protected void sublayout(int width, int height) {
			layoutChild(getField(0), getField(0).getPreferredWidth(), getField(0).getPreferredHeight());
			layoutChild(getField(1), getField(1).getPreferredWidth(), getField(1).getPreferredHeight());
			layoutChild(getField(2), getField(2).getPreferredWidth(), getField(2).getPreferredHeight());
			layoutChild(getField(3), getField(3).getPreferredWidth(), getField(3).getPreferredHeight());

			setPositionChild(getField(0), padding, (getPreferredHeight()/2)-(getField(0).getPreferredHeight()/2) );
			setPositionChild(getField(1), getPreferredWidth()-((getField(3).getPreferredWidth()+getField(2).getPreferredWidth()+8)+getField(1).getPreferredWidth()),(getPreferredHeight()/2)-(getField(1).getPreferredHeight()/2));
			setPositionChild(getField(2), getPreferredWidth()-(getField(3).getPreferredWidth()+getField(2).getPreferredWidth()+4), (getPreferredHeight()/2)-(getField(2).getPreferredHeight()/2));
			setPositionChild(getField(3), getPreferredWidth()-(getField(3).getPreferredWidth()+4), (getPreferredHeight()/2)-(getField(3).getPreferredHeight()/2));
			setExtent(getPreferredWidth(), getPreferredHeight());
		}
		public LabelField getLabel(final String text, final Font fnt, final int foreColor, final int bgColor)
		{
			return new LabelField(text, NON_FOCUSABLE) {

				public int getPreferredHeight() {
					return fnt.getHeight()+4;
				}
				public int getPreferredWidth() {
					return fnt.getAdvance(text)+4;
				}


				protected void paint(Graphics graphics) {
					graphics.setColor(foreColor);
					graphics.setFont(fnt);
					graphics.drawText(text, 2, 2);
					//super.paint(graphics);
				}
				protected void drawFocus(Graphics graphics, boolean on) {
				}

				protected void layout(int width, int height) {
					super.layout(width, height);
					setExtent(getPreferredWidth(), getPreferredHeight());
				}
				protected void paintBackground(Graphics graphics) 
				{
					int clr; 
					if(id==0) clr = 0x6c8889;
					else if(id%2!=0) {
						clr = 0x333333;
					}
					else {
						clr = 0x000000;
					}
					graphics.setColor(clr);
					graphics.fillRect(0, 0, getPreferredWidth(), getPreferredHeight());
				}
			};
		}

		public BitmapField getSmallBitmapField(final String caption, final Object object)//, final String title, final String url, final Action action)
		{
			final String cp = caption.replace(' ', '_');
			int wd = AppConstants.screenHeight<480?8:16;
			final Bitmap bitmap = ImageManager.resizeBitmap(bmp, FontLoader.getFont(AppConstants.REPORTSMARGIN_FONT).getAdvance(cp)+wd, bmp.getHeight());
			return new BitmapField(bitmap, FOCUSABLE | DrawStyle.HCENTER)
			{
				protected boolean navigationClick(int status,int time) {
					if((timer+100)<System.currentTimeMillis()){ timer = System.currentTimeMillis();
					if(!Utils.sessionAlive)
					{
						ActionInvoker.processCommand(new Action(ActionCommand.CMD_SESSION_EXPIRED,null));
					}
					else
					{	if(caption.equalsIgnoreCase("Details"))
					{
						//	   ScreenInvoker.showWaitScreen(AppConstants.loadingMessage);

						//   new ReturnStringParser(Utils.getCompanyLatestTradingDetailsURL(string),1,returnString);

						ReportsOrderView.Load = true;
						Vector vec = new Vector();
						vec.addElement("Equity Net Position");
						Vector e = new Vector();
						e.addElement(object);
						vec.addElement(e);
						//actionListener.actionPerfomed(ActionCommand.CMD_SLIDE_VIEW, vec);
						ActionInvoker.processCommand(new Action(ActionCommand.CMD_SLIDE_VIEW,vec));
						//
					}
					else if(caption.equalsIgnoreCase("Square Off"))
					{
						Hashtable h = (Hashtable)object;
						
						
						
						
						//h.get("SCRIPCODE")
						
                        LOG.print("1111 (String)h.get(\"SCRIPCODE\") : "+(String)h.get("SCRIPCODE"));
                       ScreenInvoker.showWaitScreen(AppConstants.loadingMessage);
                       new ReturnStringParser(Utils.getCompanyLatestTradingDetailsURL((String)h.get("SCRIPCODE")),1,returnString,false);
                    }
				}
				}	return super.navigationClick(status, time);
				}
				protected boolean touchEvent(TouchEvent message) {
					if(message.getEvent() == TouchEvent.CLICK) {
						if((timer+100)<System.currentTimeMillis()){ timer = System.currentTimeMillis();
						if(!Utils.sessionAlive)
					{
						ActionInvoker.processCommand(new Action(ActionCommand.CMD_SESSION_EXPIRED,null));
					}
					else
					{if(caption.equalsIgnoreCase("Details"))
					{
						ReportsOrderView.Load = true;
						//	   ScreenInvoker.showWaitScreen(AppConstants.loadingMessage);

						//   new ReturnStringParser(Utils.getCompanyLatestTradingDetailsURL(string),1,returnString);


						Vector vec = new Vector();
						vec.addElement("Equity Net Position");
						Vector e = new Vector();
						e.addElement(object);
						vec.addElement(e);
						//actionListener.actionPerfomed(ActionCommand.CMD_SLIDE_VIEW, vec);
						ActionInvoker.processCommand(new Action(ActionCommand.CMD_SLIDE_VIEW,vec));
						//
					}
					else if(caption.equalsIgnoreCase("Square Off"))
					{
						Hashtable h = (Hashtable)object;
                        LOG.print("2222 (String)h.get(\"SCRIPCODE\") : "+(String)h.get("SCRIPCODE"));
                       ScreenInvoker.showWaitScreen(AppConstants.loadingMessage);
                       new ReturnStringParser(Utils.getCompanyLatestTradingDetailsURL((String)h.get("SCRIPCODE")),1,returnString,false);
                    }
				}}}return super.touchEvent(message);
				}
				private boolean isFocused = false;
				protected void onFocus(int direction) 
				{
					isFocused = true;
					invalidate();
				}
				protected void onUnfocus() {
					isFocused = false;
					invalidate();
				}
				/*protected void paintBackground(Graphics graphics) {
					graphics.setBackgroundColor(Color.BLACK);
					graphics.clear();
				}*/
				protected void paint(Graphics graphics) 
				{
					if(isFocused) {
						graphics.setColor(Color.ORANGE);
					} else {
						graphics.setColor(0xeeeeee);
					}
					graphics.setFont(FontLoader.getFont(AppConstants.REPORTS_FONT));
					//graphics.fillRect(0, 0, getWidth(), getHeight());
					graphics.drawBitmap(0,0, bitmap.getWidth(), bitmap.getHeight(), bitmap, 0, 0);
					graphics.drawText(caption,(bitmap.getWidth()/2)-(getFont().getAdvance(caption)/2),(bitmap.getHeight()/2)-(getFont().getHeight()/2));

				}
				protected void drawFocus(Graphics graphics, boolean on) 
				{

				}
				public Font getFont() { return FontLoader.getFont(AppConstants.REPORTSMARGIN_FONT);}
				public int getPreferredHeight() {return  bitmap.getHeight();}//(getFont().getHeight()*2)-(getFont().getHeight()/2);}
			public int getPreferredWidth() {
				return bitmap.getWidth();
			}
			protected void layout(int arg0, int arg1) {
				setExtent(getPreferredWidth(), getPreferredHeight());
			}
			};
		}

		public LabelField getSmallBitmapFieldBlank(final String caption, final int color)//, final String title, final String url, final Action action)
		{
			return new LabelField("", NON_FOCUSABLE | DrawStyle.HCENTER)
			{

				protected boolean navigationClick(int status,int time) {
					return super.navigationClick(status, time);
				}
				protected boolean touchEvent(TouchEvent message) {
					return super.touchEvent(message);
				}
				private boolean isFocused = false;
				protected void onFocus(int direction) 
				{
					isFocused = true;
					invalidate();
				}
				protected void onUnfocus() {
					isFocused = false;
					invalidate();
				}
				protected void paintBackground(Graphics graphics) {
					int clr;
					if(id==0) clr = 0x6c8889;
					else clr = color;
					graphics.setColor(clr);
					graphics.fillRect(0, 0, getPreferredWidth(), getPreferredHeight());
					return;

				}
				protected void paint(Graphics graphics) 
				{
					int clr; 
					if(id==0) clr = 0x6c8889;
					else if(id%2!=0) {
						clr = 0x333333;
					}
					else {
						clr = 0x000000;
					}
					graphics.setColor(clr);
					graphics.fillRect(0, 0, getPreferredWidth(), getPreferredHeight());
					// graphics.setColor(clr);
					//  graphics.fillRect(0, 0, getPreferredWidth(), getPreferredHeight());
				}
				protected void drawFocus(Graphics graphics, boolean on) 
				{

				}
				public Font getFont() { return FontLoader.getFont(AppConstants.REPORTSMARGIN_FONT);}
				public int getPreferredHeight() {return  bmp.getHeight();}//(getFont().getHeight()*2)-(getFont().getHeight()/2);}
			public int getPreferredWidth() {
				return FontLoader.getFont(AppConstants.REPORTSMARGIN_FONT).getAdvance(caption)+AppConstants.screenHeight<480?8:16;
			}
			protected void layout(int arg0, int arg1) {
				setExtent(getPreferredWidth(), getPreferredHeight());
			}
			};
		}


	}

	public void setData(Vector vector, int id) {
		if(id == 51)
		{
			TradeNowMainScreenCM trade = new TradeNowMainScreenCM(bannerData);
			trade.modifyScreenForNetPosition("ReportNetPosition",vector, id, false);
		}
	}

	public void setReturnString(String string, int id) {
		Vector v = HomeJsonParser.getVector(string);
		bannerData = (HomeJson) v.elementAt(0);
		//OK String url = AppConstants.domainUrl +"orderEQ.php?ccode="+(String)hashData.get("SCRIPCODE")+"&exchange="+(String)hashData.get("EXCHANGE")+"&action="+(String)hashData.get("action")+"&qty="+(String)hashData.get("NetQty")+"&custId="+UserInfo.getUserID()+"&page=netpos&debug=2";
		//asdasdasdasdasdasds
/////////////////////////////////
		/////////Calculate Action
		String action = "";
		try {
			String netQty = (String)hashData.get("NETQTY");
			String productType = (String)hashData.get("PRODUCTTYPE");
			int netQtyInt = Integer.parseInt(netQty);
			if(productType.equalsIgnoreCase("Investment") && netQtyInt > 0){
			    action = "S";
			}else if(productType.equalsIgnoreCase("Investment") && netQtyInt < 0){
			     action = "B";
			}else if(productType.equalsIgnoreCase("Margin") && netQtyInt > 0){
			     action = "SM";       
			}else if(productType.equalsIgnoreCase("Margin") && netQtyInt < 0){
			     action = "BM";      
			}
		} catch (NumberFormatException e1) {
			action = "";
		}
		//String url = AppConstants.domainUrl +"orderEQ.php?ccode="+(String)hashData.get("SCRIPCODE")+"&exchange="+(String)hashData.get("EXCHANGE")+"&action="+action+"&qty="+(String)hashData.get("NETQTY")+"&custId="+UserInfo.getUserID()+"&page=netpos&debug=2";
		String scripCode = (String)hashData.get("SCRIPCODE");
		scripCode = URLEncode.replace(scripCode);
		String url = AppConstants.domainUrl +"SK_android/controller.php?RequestId=steqnp02&ccode="+scripCode+"&exchange="+(String)hashData.get("EXCHANGE")+"&qty="+(String)hashData.get("NETQTY")+"&action="+action+"&custId="+UserInfo.getUserID()+"&page=netpos";
		LOG.print(" URL : "+url);
		new TradeNowMainParser(url,returnDataWithId,51);

	}
}
