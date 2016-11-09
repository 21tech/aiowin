package com.detailed;

import java.awt.Image;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;
import javax.swing.JFrame;

import org.cef.CefApp;
import org.cef.OS;

import com.detailed.app.Apps;

public class AppMain {
	public static void main(String[] args) {
		boolean osrEnabledArg = OS.isLinux();
		String cookiePath = null;
		for (String arg : args) {
			arg = arg.toLowerCase();
			if (!OS.isLinux() && arg.equals("--off-screen-rendering-enabled")) {
				osrEnabledArg = true;
			} else if (arg.startsWith("--cookie-path=")) {
				cookiePath = arg.substring("--cookie-path=".length());
				File testPath = new File(cookiePath);
				if (!testPath.isDirectory() || !testPath.canWrite()) {
					System.out.println("Can't use " + cookiePath
							+ " as cookie directory. Check if it exists and if it is writable");
					cookiePath = null;
				} else {
					System.out.println("Storing cookies in " + cookiePath);
				}
			}
		}
		String loadUrl = Apps.getRequestUrl().split("=")[1];
		System.out.println("Address is " + loadUrl);
		System.out.println("Offscreen rendering " + (osrEnabledArg ? "enabled" : "disabled"));
		final AppFrame frame = new AppFrame(osrEnabledArg, cookiePath, args, loadUrl);
		frame.addWindowListener(new WindowAdapter() {
			@Override
			public void windowClosing(WindowEvent e) {
				CefApp.getInstance().dispose();
				frame.dispose();
			}
		});
		try {
			Image image = ImageIO.read(frame.getClass().getResource("/img/logo.png"));
			// frame.setExtendedState(JFrame.MAXIMIZED_BOTH);
			// frame.setAlwaysOnTop(true);
			// frame.setUndecorated(true);
			frame.setIconImage(image);
			frame.setSize(800, 600);
			frame.setVisible(true);
		} catch (IOException e1) {
			e1.printStackTrace();
		}

	}

}
