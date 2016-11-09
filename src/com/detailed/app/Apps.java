package com.detailed.app;

public class Apps {
	static {
		System.loadLibrary("libaio");
	}

	public static String getValidateCode() {
		try {
			return N_Init();
		} catch (UnsatisfiedLinkError localUnsatisfiedLinkError) {
			localUnsatisfiedLinkError.printStackTrace();
		}
		return null;
	}

	public static String getRequestUrl() {
		try {
			return N_GetRequestUrl();
		} catch (UnsatisfiedLinkError localUnsatisfiedLinkError) {
			localUnsatisfiedLinkError.printStackTrace();
		}
		return null;

	}

	public static String readCardNumber() {

		try {
			return N_ReadCardNumber();
		} catch (UnsatisfiedLinkError localUnsatisfiedLinkError) {
			localUnsatisfiedLinkError.printStackTrace();
		}
		return null;

	}
	
	public static String decCardMoney(long nMoney,int TerminalNo,String operate_time ){
		
		try {
			return N_DecCardMoney(nMoney,TerminalNo,operate_time);
		} catch (UnsatisfiedLinkError localUnsatisfiedLinkError) {
			localUnsatisfiedLinkError.printStackTrace();
		}
		return null;
		
	}

	private final native static String N_GetRequestUrl();

	private final native static String N_Init();

	private final native static String N_ReadCardNumber();
	
	private final native static String N_DecCardMoney(long nMoney,int TerminalNo,String operate_time );
}
