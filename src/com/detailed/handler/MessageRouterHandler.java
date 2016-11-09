// Copyright (c) 2014 The Chromium Embedded Framework Authors. All rights
// reserved. Use of this source code is governed by a BSD-style license that
// can be found in the LICENSE file.

package com.detailed.handler;

import org.cef.browser.CefBrowser;
import org.cef.callback.CefQueryCallback;
import org.cef.handler.CefMessageRouterHandlerAdapter;
import org.json.JSONException;
import org.json.JSONObject;

import com.detailed.app.Apps;

public class MessageRouterHandler extends CefMessageRouterHandlerAdapter {
	private String msg;
	RCTask r;

	@Override
	public boolean onQuery(CefBrowser browser, long query_id, String request, boolean persistent,
			CefQueryCallback callback) {
		System.out.println("Get Request Head :" + request);

		if (request.indexOf("Request:init") == 0) {
			msg = Apps.getValidateCode();
			callback.success(new StringBuilder(msg).toString());
			return true;
		}

		if (request.indexOf("Request:RC") == 0) {
			String action = request.split("-")[1];
			msg = "200";
			if (action.equals("start")) {
				if (r != null) {
					if (r.isAlive()) {
						r.dStop();
					}
				}
				System.out.println("status : new and start()");
				r = new RCTask(browser);
				r.start();

			} else {
				if (r != null) {
					System.out.println("status : stop");
					r.dStop();
				}
			}

			callback.success(new StringBuilder(msg).toString());
			return true;
		}

		if (request.indexOf("Request:DCM") == 0) {
			String data = request.split("=")[1];
			try {
				JSONObject jsonObject = new JSONObject(data.trim());
				msg = Apps.decCardMoney(jsonObject.getLong("nMoney"), jsonObject.getInt("Tn"), jsonObject.getString("operate_time"));
				System.out.println(new StringBuilder(msg).toString());
				callback.success(new StringBuilder(msg).toString());
			} catch (JSONException e) {
				e.printStackTrace();
			}

			return true;
		}
		return false;
	}

	class RCTask extends Thread {
		private volatile int status = 1;
		CefBrowser browser;
		private final int STOP = -1;
		private final int SUSPEND = 0;
		private final int RESUME = 1;
		private String rs;

		public RCTask(CefBrowser browser) {
			this.browser = browser;
		}

		@Override
		public synchronized void run() {
			while (status != STOP) {
				try {
					if (status == SUSPEND) {
						System.out.println("挂起...");
						wait();
					} else {
						System.out.println("读取中...");
						rs = Apps.readCardNumber();
						if (!"-1".equals(rs)) {
							browser.executeJavaScript(
									"document.getElementsByTagName('input')[0].value = '" + rs
											+ "';	$('input').removeAttr('id');$('#i_pWord input').attr('id', 'writing');",
									"", 0);
							status = STOP;
						}
						Thread.sleep(3000);
					}
				} catch (InterruptedException e) {
					System.out.println("线程异常终止...");
				}
			}
			System.out.println("读取结束...");

		}

		public synchronized void dResume() {
			status = RESUME;
			notifyAll();
		}

		public void dSuspend() {
			status = SUSPEND;
		}

		public void dStop() {
			status = STOP;
		}
	}
}
