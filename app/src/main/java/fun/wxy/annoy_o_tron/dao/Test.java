package fun.wxy.annoy_o_tron.dao;

// THIS CODE IS GENERATED BY greenDAO, DO NOT EDIT. Enable "keep" sections if you want to edit. 
/**
 * Entity mapped to table "TEST".
 */
public class Test {

    private Long id;
    private String name;
    private int age;
    /** Not-null value. */
    private String sex;

    public Test() {
    }

    public Test(Long id) {
        this.id = id;
    }

    public Test(Long id, String name, int age, String sex) {
        this.id = id;
        this.name = name;
        this.age = age;
        this.sex = sex;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getAge() {
        return age;
    }

    public void setAge(int age) {
        this.age = age;
    }

    /** Not-null value. */
    public String getSex() {
        return sex;
    }

    /** Not-null value; ensure this value is available before it is saved to the database. */
    public void setSex(String sex) {
        this.sex = sex;
    }

}