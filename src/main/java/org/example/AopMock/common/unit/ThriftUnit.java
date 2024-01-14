package org.example.AopMock.common.unit;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.SuperBuilder;
import org.apache.thrift.TBase;

@EqualsAndHashCode(callSuper = true)
@Data
@SuperBuilder
public class ThriftUnit extends ReqRecordUnit{


    private String signature;
    private TBase arg;
    private TBase result;
    @Override
    public RecordInterfaceType type() {
        return RecordInterfaceType.thrift;
    }

    @Override
    public UniqueKey genUniqueKey() {
        return new UniqueKey().add(signature).add(arg);
    }
}
