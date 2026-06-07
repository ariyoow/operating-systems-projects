#include <stdio.h>
#include <stdlib.h>
#include <sys/shm.h>
#include "shared_defs.h"

int main(int argc, char *argv[]) {
    if(argc != 6) {
        fprintf(stderr, "Usage: %s <shmid> <start_index> <count> <filename> <ith>\n", argv[0]);
        exit(1);
    }

    int shmid = atoi(argv[1]);
    int start_index = atoi(argv[2]);
    int count = atoi(argv[3]);
    char *filename = argv[4];
    int ith = atoi(argv[5]);


    child_data *shm_ptr = (child_data*) shmat(shmid, NULL, 0);
    if(shm_ptr == (void*) -1) { perror("shmat"); exit(1); }

    FILE *fp = fopen(filename, "r");
    if(!fp) { perror("fopen"); exit(1); }

    int total_numbers;
    fscanf(fp, "%d", &total_numbers);

    int num;
    int local_max = -2147483648;
    long local_sum = 0;


    for(int i=0; i<start_index; i++) fscanf(fp, "%d", &num);

    for(int i=0; i<count; i++) {
        if(fscanf(fp, "%d", &num) != 1) break;
        local_sum += num;
        if(num > local_max) local_max = num;
    }

    shm_ptr[ith].local_sum = local_sum;
    shm_ptr[ith].local_count = count;
    shm_ptr[ith].local_avg = local_sum / count;
    shm_ptr[ith].local_max = local_max;
    

    fclose(fp);
    shmdt(shm_ptr);

    return 0;
}
