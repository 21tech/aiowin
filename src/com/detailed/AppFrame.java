package com.detailed;

import java.awt.BorderLayout;

import javax.swing.JFrame;
import javax.swing.JPanel;

import org.cef.CefApp;
import org.cef.CefApp.CefVersion;
import org.cef.CefClient;
import org.cef.CefSettings;
import org.cef.browser.CefBrowser;
import org.cef.browser.CefMessageRouter;
import org.cef.browser.CefRequestContext;
import org.cef.handler.CefDisplayHandlerAdapter;
import org.cef.handler.CefLoadHandlerAdapter;
import org.cef.handler.CefRequestContextHandlerAdapter;
import org.cef.network.CefCookieManager;

import com.detailed.handler.AppHandler;
import com.detailed.handler.ContextMenuHandler;
import com.detailed.handler.MessageRouterHandler;
import com.detailed.handler.MessageRouterHandlerEx;
import com.detailed.handler.RequestHandler;

public class AppFrame extends JFrame {

	/**
	 * 
	 */
	private static final long serialVersionUID = -7358209277432919497L;

	private final CefClient client_;
	private String errorMsg_ = "";
	private final CefBrowser browser_;
	private final CefCookieManager cookieManager_;
	

	public AppFrame(boolean osrEnabled, String cookiePath, String[] args,String url) {
		CefSettings settings = new CefSettings();
		settings.windowless_rendering_enabled = osrEnabled;
		settings.background_color = settings.new ColorType(100, 255, 242, 211);
		CefApp myApp = CefApp.getInstance(args, settings);
		CefVersion version = myApp.getVersion();
		//System.out.println("Using:\n" + version);
		CefApp.addAppHandler(new AppHandler(args));
        
		client_ = myApp.createClient();

		client_.addContextMenuHandler(new ContextMenuHandler(this));
//		client_.addDragHandler(new DragHandler());
//		client_.addGeolocationHandler(new GeolocationHandler(this));
//		client_.addJSDialogHandler(new JSDialogHandler());
//		client_.addKeyboardHandler(new KeyboardHandler());
		client_.addRequestHandler(new RequestHandler(this));
		CefMessageRouter msgRouter = CefMessageRouter.create();
		msgRouter.addHandler(new MessageRouterHandler(), true);
		msgRouter.addHandler(new MessageRouterHandlerEx(client_), false);
		client_.addMessageRouter(msgRouter);
		client_.addDisplayHandler(new CefDisplayHandlerAdapter() {
			@Override
			public void onAddressChange(CefBrowser browser, String url) {
				//System.out.println("-----startOnAddre------");
			}

			@Override
			public void onTitleChange(CefBrowser browser, String title) {
				//System.out.println("-----startOnTitleC------");
			}

			@Override
			public void onStatusMessage(CefBrowser browser, String value) {
				//System.out.println("-----startOnStatusMe------");
			}
		});
		client_.addLoadHandler(new CefLoadHandlerAdapter() {
			@Override
			public void onLoadingStateChange(CefBrowser browser, boolean isLoading, boolean canGoBack,
					boolean canGoForward) {
				System.out.println(errorMsg_);
				if (!errorMsg_.isEmpty()) {
					browser.loadString(errorMsg_, url);
					errorMsg_ = "";
				}
			}
			
			@Override
			public void onLoadError(CefBrowser browser, int frameIdentifer, ErrorCode errorCode, String errorText,
					String failedUrl) {
				if (errorCode != ErrorCode.ERR_NONE && errorCode != ErrorCode.ERR_ABORTED) {
					errorMsg_ = "<html><head>";
					errorMsg_ += "<title>Error while loading</title>";
					errorMsg_ += "</head><body>";
					errorMsg_ += "<h1>" + errorCode + "</h1>";
					errorMsg_ += "<h3>Failed to load " + failedUrl + "</h3>";
					errorMsg_ += "<p>" + (errorText == null ? "" : errorText) + "</p>";
					errorMsg_ += "</body></html>";
					browser.stopLoad();
				}
			}
		});
		CefRequestContext requestContext = null;
		if (cookiePath != null) {
			cookieManager_ = CefCookieManager.createManager(cookiePath, false);
			requestContext = CefRequestContext.createContext(new CefRequestContextHandlerAdapter() {
				@Override
				public CefCookieManager getCookieManager() {
					return cookieManager_;
				}
			});
		} else {
			cookieManager_ = CefCookieManager.getGlobalManager();
		}
		browser_ = client_.createBrowser(url, osrEnabled, false, requestContext);
		getContentPane().add(createContentPanel(), BorderLayout.CENTER);
	}

	private JPanel createContentPanel() {
		JPanel contentPanel = new JPanel(new BorderLayout());
		contentPanel.add(browser_.getUIComponent(), BorderLayout.CENTER);
		return contentPanel;
	}
}
