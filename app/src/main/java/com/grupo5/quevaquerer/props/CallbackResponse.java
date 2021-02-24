package com.grupo5.quevaquerer.props;

public interface CallbackResponse {
    void success(String data, String option);

    void fail(String data);
}
