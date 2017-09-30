/*
 * Copyright (C) 2008 ZXing authors
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.zxinglib.android;

/**
 * The main settings activity.
 * 设置相关的常量
 *
 * @author dswitkin@google.com (Daniel Switkin)
 * @author Sean Owen
 */
public class Config {
	public static final int DECODE = 1;
	public static final int QUIT = 2;
	public static final int RESTART_PREVIEW = 3;
	public static final int DECODE_SUCCEEDED = 4;
	public static final int LAUNCH_PRODUCT_QUERY = 5;
	public static final int DECODE_FAILED = 6;
	
	public static boolean decode1dProduct = true;
	public static boolean decode1dIndustrial = true;
	public static boolean decodeQr = true;
	public static boolean decodeDataMatrix = true;
	public static boolean decodeAztec;
	public static boolean decodePdf417;
	public static boolean playBeep = true;
	public static boolean vibrate;
}
