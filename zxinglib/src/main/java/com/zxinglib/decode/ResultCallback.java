package com.zxinglib.decode;

import com.google.zxing.Result;

/**
 * Created by zeng on 2017/2/15.
 */

public interface ResultCallback {
    void onResult(Result result);
}
