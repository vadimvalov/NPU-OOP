struct Student {
    id: u32,
    name: String,
    grade: u32,
}

struct Group {
    title: String,
    students: Vec<Student>,
}

fn main() {
    let group = Group {
        title: String::from("CST-2501"),
        students: vec![
            Student { id: 1, name: String::from("vadim"), grade: 100 },
            Student { id: 2, name: String::from("daulet"),   grade: 100 },
        ],
    };

    println!("group: {}", group.title);
    for s in &group.students {
        println!("  student: {}, grade: {}", s.name, s.grade);
    }
} // here automatically drop(group)