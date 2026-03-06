#include <stdio.h>
#include <stdlib.h>
#include <string.h>

typedef struct {
    int   id;
    char* name;
    int   grade;
} Student;

int main() {
    int is_freed = 0;

    Student* s = malloc(sizeof(Student));
    s->id      = 1;
    s->name    = malloc(strlen("Alice") + 1);
    strcpy(s->name, "vadim");
    s->grade   = 100;

    printf("student: %s, grade: %d\n", s->name, s->grade);

    free(s->name);
    free(s);
    is_freed = 1;

    if (!is_freed) {
        printf("haha memory leak\n");
        while(1);
    }

    printf("wow no memory leak good job\n");
    return 0;
}