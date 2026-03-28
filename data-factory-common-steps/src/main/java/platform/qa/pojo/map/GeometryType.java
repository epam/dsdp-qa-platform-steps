package platform.qa.pojo.map;

public enum GeometryType {
    POLYGON("polygon", "polygon"),
    LINE("line","path"),
    POINT("point", "point");

    private final String typeGeometryApi;
    private final String typeGeometryFunctionDb;

    GeometryType(String typeGeometryApi, String typeGeometryFunctionDb) {
        this.typeGeometryApi = typeGeometryApi;
        this.typeGeometryFunctionDb = typeGeometryFunctionDb;
    }

    public String getTypeGeometryApi() {
        return typeGeometryApi;
    }

    public String gettypeGeometryFunctionDb() {
        return typeGeometryFunctionDb;
    }
}
