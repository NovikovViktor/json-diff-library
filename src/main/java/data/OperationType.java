package data;

/**
 * @author novikov_vi on 03.07.2020
 */
public enum OperationType {

    ADD("add"),
    REPLACE("replace"),
    REMOVE("remove");

    public final String op;

    OperationType(String op){
        this.op = op;
    }
}
