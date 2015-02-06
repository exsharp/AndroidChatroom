package com.example.zfliu.chatroom.friendlist;

/**
 * Created by sharp on 2/3/2015.
 */
public class Apple {

    /** 用户名   */
    private String name;
    /** 头像    */
    private Integer portrait;
    /** 用户id  */
    private int id;

    public int getId() {
        return id;
    }
    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }
    public void setName(String name) {
        this.name = name;
    }

    public Integer getPortrait() {
        return portrait;
    }
    public void setPortrait(Integer portrait) {
        this.portrait = portrait;
    }

    public Apple(String name, Integer portrait,Integer state,int id) {
        this.name = name;
        this.portrait = portrait;
        this.id = id;
    }

    public Apple(String name,int id) {
        this.name = name;
        this.id = id;
    }

    public Apple() {
        super();
    }

    @Override
    public String toString() {
        return getName();
    }

}
