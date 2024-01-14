package org.example.AopMock.common.unit;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class UniqueKey {
    private List<Object> keys = new ArrayList<>();

    public UniqueKey(){}

    public UniqueKey(List<Object> keys){
        this.keys = keys;
    }

    public UniqueKey add(Object obj){
        keys.add(obj);
        return this;
    }

    @Override
    public boolean equals(Object o){
        if (this == o){
            return true;
        }
        if (o==null || getClass()!=o.getClass()){
            return false;
        }
        UniqueKey uniqueKey = (UniqueKey) o;
        return Objects.equals(keys,uniqueKey.keys);
    }

    @Override
    public int hashCode(){
        return Objects.hashCode(keys);
    }
}
