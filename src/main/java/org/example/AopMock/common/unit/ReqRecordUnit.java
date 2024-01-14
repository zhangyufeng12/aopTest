package org.example.AopMock.common.unit;

import lombok.Data;
import lombok.experimental.SuperBuilder;

@Data
@SuperBuilder
public abstract class ReqRecordUnit {
    public ReqRecordUnit(){}

    protected String service;
    protected String traceId;

    public abstract RecordInterfaceType type();

    public abstract UniqueKey genUniqueKey();
}
