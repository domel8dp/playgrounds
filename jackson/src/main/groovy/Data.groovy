class Data {
    String strValue
    double dblValue
    BigDecimal bigDValue
    int intValue
    boolean boolValue

    @Override
    public String toString() {
        return "Data{" +
                "strValue='" + strValue + '\'' +
                ", dblValue=" + dblValue +
                ", bigDValue=" + bigDValue +
                ", intValue=" + intValue +
                ", boolValue=" + boolValue +
                '}';
    }
}