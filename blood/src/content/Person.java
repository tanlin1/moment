package content;

/**
 * Created by Administrator on 2014/8/12.
 */
public class Person {
	public Person(String id, String name) {
		this.id = id;
		this.name = name;
	}

	public Person(String id, String name, String num) {
		this.id = id;
		this.name = name;
		this.num = num;
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getNum() {
		return num;
	}

	public void setNum(String num) {
		this.num = num;
	}

	String id;
	String name;
	String num;
}
