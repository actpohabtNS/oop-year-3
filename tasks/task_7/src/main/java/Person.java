import java.io.Serializable;

public class Person implements Serializable {
    String name;
    int age;
    int weight;

    public Person(String name, int age, int weight) {
        this.name = name;
        this.age = age;
        this.weight = weight;
    }

    @Override
    public String toString() {
        return "Person{" +
                "name=" + name +
                ", age=" + age +
                ", weight='" + weight + '\'' +
                '}';
    }
}
