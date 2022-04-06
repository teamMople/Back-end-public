package com.hanghae99.boilerplate.chat.trace.logtrace;

import com.hanghae99.boilerplate.chat.trace.TraceStatus;

public interface LogTrace {

    TraceStatus begin(String message);

    void end(TraceStatus status);

    void exception(TraceStatus status, Exception e);

}
