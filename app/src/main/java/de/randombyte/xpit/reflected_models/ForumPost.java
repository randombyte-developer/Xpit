package de.randombyte.xpit.reflected_models;

public class ForumPost extends ReflectedModel {

    public ForumPost(Object objectOfTargetApp) {
        super(objectOfTargetApp);
    }

    public int getAuthorId() {
        return getIntField("authorId");
    }

    public String getAuthorName() {
        return (String) getObjectField("authorName");
    }

    public void setAuthorName(String authorName) {
        setObjectField("authorName", authorName);
    }
}
