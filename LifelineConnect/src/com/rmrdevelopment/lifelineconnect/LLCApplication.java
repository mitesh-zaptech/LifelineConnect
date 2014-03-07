package com.rmrdevelopment.lifelineconnect;

import java.util.ArrayList;
import java.util.HashMap;

import android.app.Application;

public class LLCApplication extends Application {

	public static ArrayList<HashMap<String, String>> VoicemailList = new ArrayList<HashMap<String, String>>();
	public static ArrayList<HashMap<String, String>> TagsList = new ArrayList<HashMap<String, String>>();
	public static ArrayList<HashMap<String, String>> DistroLists = new ArrayList<HashMap<String, String>>();
	public static ArrayList<HashMap<String, String>> DistroMembersLists = new ArrayList<HashMap<String, String>>();
	public static ArrayList<HashMap<String, String>> WireSearchUsers = new ArrayList<HashMap<String, String>>();

	public static String speaker = "1";
	public static String username = "";
	public static String password = "";
	public static int remember = 0;
	public static int userloggedin = 0;
	public static String UserId = "";
	public static String CanSeeDownline = "";
	public static String UplineName = "";
	public static String UplineUserID = "";
	public static String ReceiveNotifications = "";
	public static String countMessagesDownline = "";
	public static String countMessagesUpline = "";
	public static int position;
	public static boolean flagRefresh = false;
	
	public static int currentDownlinePosition;
	
	public static String getSpeaker() {
		return speaker;
	}

	public static void setSpeaker(String speaker) {
		LLCApplication.speaker = speaker;
	}

	public static int getCurrentDownlinePosition() {
		return currentDownlinePosition;
	}

	public static void setCurrentDownlinePosition(int currentDownlinePosition) {
		LLCApplication.currentDownlinePosition = currentDownlinePosition;
	}

	public static ArrayList<HashMap<String, String>> getWireSearchUsers() {
		return WireSearchUsers;
	}

	public static void setWireSearchUsers(
			ArrayList<HashMap<String, String>> wireSearchUsers) {
		WireSearchUsers = wireSearchUsers;
	}

	public static ArrayList<HashMap<String, String>> getDistroMembersLists() {
		return DistroMembersLists;
	}

	public static void setDistroMembersLists(
			ArrayList<HashMap<String, String>> distroMembersLists) {
		DistroMembersLists = distroMembersLists;
	}

	public static boolean isFlagRefresh() {
		return flagRefresh;
	}

	public static void setFlagRefresh(boolean flagRefresh) {
		LLCApplication.flagRefresh = flagRefresh;
	}

	public static int getPosition() {
		return position;
	}

	public static void setPosition(int position) {
		LLCApplication.position = position;
	}

	public static String getCountMessagesDownline() {
		return countMessagesDownline;
	}

	public static String getCountMessagesUpline() {
		return countMessagesUpline;
	}

	public static void setCountMessagesDownline(String countMessagesDownline) {
		LLCApplication.countMessagesDownline = countMessagesDownline;
	}

	public static void setCountMessagesUpline(String countMessagesUpline) {
		LLCApplication.countMessagesUpline = countMessagesUpline;
	}

	public static ArrayList<HashMap<String, String>> getTagsList() {
		return TagsList;
	}

	public static ArrayList<HashMap<String, String>> getDistroLists() {
		return DistroLists;
	}

	public static void setDistroLists(
			ArrayList<HashMap<String, String>> distroLists) {
		DistroLists = distroLists;
	}

	public static void setTagsList(ArrayList<HashMap<String, String>> tagsList) {
		TagsList = tagsList;
	}

	public static ArrayList<HashMap<String, String>> getVoicemailList() {
		return VoicemailList;
	}

	public static void setVoicemailList(
			ArrayList<HashMap<String, String>> voicemailList) {
		VoicemailList = voicemailList;
	}

	public static String getCanSeeDownline() {
		return CanSeeDownline;
	}

	public static String getUplineName() {
		return UplineName;
	}

	public static String getUplineUserID() {
		return UplineUserID;
	}

	public static String getReceiveNotifications() {
		return ReceiveNotifications;
	}

	public static void setCanSeeDownline(String canSeeDownline) {
		CanSeeDownline = canSeeDownline;
	}

	public static void setUplineName(String uplineName) {
		UplineName = uplineName;
	}

	public static void setUplineUserID(String uplineUserID) {
		UplineUserID = uplineUserID;
	}

	public static void setReceiveNotifications(String receiveNotifications) {
		ReceiveNotifications = receiveNotifications;
	}

	public static int getUserloggedin() {
		return userloggedin;
	}

	public static void setUserloggedin(int userloggedin) {
		LLCApplication.userloggedin = userloggedin;
	}

	public static int getRemember() {
		return remember;
	}

	public static void setRemember(int remember) {
		LLCApplication.remember = remember;
	}

	public static String getUsername() {
		return username;
	}

	public static void setUsername(String username) {
		LLCApplication.username = username;
	}

	public static String getPassword() {
		return password;
	}

	public static void setPassword(String password) {
		LLCApplication.password = password;
	}

	public static String getUserId() {
		return UserId;
	}

	public static void setUserId(String userId) {
		UserId = userId;
	}
}
