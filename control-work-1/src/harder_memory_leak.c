#include <stdio.h>
#include <stdlib.h>
#include <string.h>

typedef struct {
    int   id;
    char* name;
    int   grade;
} Student;

typedef struct {
    char*     title;
    Student** students;
    int       count;
} Group;

int main() {
    int is_freed = 0;

    Student* s1 = malloc(sizeof(Student));
    s1->id = 1; s1->name = malloc(strlen("vadim") + 1); strcpy(s1->name, "vadim"); s1->grade = 100;

    Student* s2 = malloc(sizeof(Student));
    s2->id = 2; s2->name = malloc(strlen("daulet") + 1);   strcpy(s2->name, "daulet");   s2->grade = 100;

    Group* g = malloc(sizeof(Group));
    g->title    = malloc(strlen("CST-2501") + 1);
    strcpy(g->title, "CST-2501");
    g->count    = 2;
    g->students = malloc(sizeof(Student*) * g->count);
    g->students[0] = s1;
    g->students[1] = s2;

    printf("group: %s\n", g->title);
    for (int i = 0; i < g->count; i++)
        printf("  student: %s, grade: %d\n", g->students[i]->name, g->students[i]->grade);

    for (int i = 0; i < g->count; i++) {
        free(g->students[i]->name);
        free(g->students[i]);
    }
    free(g->students);
    free(g->title);
    free(g);
    is_freed = 1;

    if (!is_freed) {
        printf("haha memory leak\n");
        while(1);
    }

    printf("wow no memory leak good job\n");
    return 0;
}