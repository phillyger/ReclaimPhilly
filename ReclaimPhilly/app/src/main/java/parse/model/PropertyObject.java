package parse.model;


import com.google.android.gms.maps.model.LatLng;
import com.google.gson.annotations.Expose;
import com.google.maps.android.clustering.ClusterItem;


public class PropertyObject implements ClusterItem {

    private LatLng mPosition;

//    public PropertyObject(double lat, double lng) {
//        mPosition = new LatLng(lat, lng);
//    }

    @Override
    public LatLng getPosition() {

        Geopoint p = getGeopoint();
        mPosition = new LatLng(p.getLatitude(), p.getLongitude());
        return mPosition;
    }

    @Expose
    private Geopoint geopoint;
    @Expose
    private String type;
    @Expose
    private String address;
    @Expose
    private Photo photo;
    @Expose
    private String createdAt;
    @Expose
    private String updatedAt;
    @Expose
    private String objectId;

    public Geopoint getGeopoint() {
        return geopoint;
    }

    public void setGeopoint(Geopoint geopoint) {
        this.geopoint = geopoint;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public Photo getPhoto() {
        return photo;
    }

    public void setPhoto(Photo photo) {
        this.photo = photo;
    }

    public String getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(String createdAt) {
        this.createdAt = createdAt;
    }

    public String getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(String updatedAt) {
        this.updatedAt = updatedAt;
    }

    public String getObjectId() {
        return objectId;
    }

    public void setObjectId(String objectId) {
        this.objectId = objectId;
    }


    public class Geopoint {

        @Expose
        private String __type;
        @Expose
        private Double latitude;
        @Expose
        private Double longitude;

        public String get__type() {
            return __type;
        }

        public void set__type(String __type) {
            this.__type = __type;
        }

        public Double getLatitude() {
            return latitude;
        }

        public void setLatitude(Double latitude) {
            this.latitude = latitude;
        }

        public Double getLongitude() {
            return longitude;
        }

        public void setLongitude(Double longitude) {
            this.longitude = longitude;
        }

    }


    public class Photo {

        @Expose
        private String __type;
        @Expose
        private String name;
        @Expose
        private String url;

        public String get__type() {
            return __type;
        }

        public void set__type(String __type) {
            this.__type = __type;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public String getUrl() {
            return url;
        }

        public void setUrl(String url) {
            this.url = url;
        }

    }

}