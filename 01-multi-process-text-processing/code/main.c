#include <stdio.h>
#include <stdlib.h>
#include <unistd.h>
#include <sys/types.h>
#include <sys/wait.h>
#include <sys/ipc.h>
#include <sys/shm.h>
#include <sys/time.h>
#include "shared_defs.h"


int main(int argc, char *argv[]) {
    if(argc != 3) {
        fprintf(stderr, "Usage: %s <num_processes> <input_file>\n", argv[0]);
        exit(1);
    }


    int num_processes = atoi(argv[1]);
    char *filename = argv[2];

    FILE *fp = fopen(filename, "r");
    if(!fp) { perror("fopen"); exit(1); }

    int total_numbers;
    fscanf(fp, "%d", &total_numbers);
    fclose(fp);

    int numbers_per_process = total_numbers / num_processes;
    int remaining = total_numbers % num_processes;

    key_t key = ftok(".", 'A');
    int shmid = shmget(key, num_processes * sizeof(child_data), IPC_CREAT | 0666);
    if(shmid < 0) { perror("shmget"); exit(1); }

    struct timeval start_time, end_time;
    gettimeofday(&start_time, NULL);


    for(int i=0; i<num_processes; i++) {
        pid_t pid = fork();
        if(pid == 0) {
            int count = numbers_per_process + (i < remaining ? 1 : 0);
            char shmid_str[16], start_index_str[16], count_str[16], ith[16];
            sprintf(shmid_str, "%d", shmid);
            sprintf(start_index_str, "%d", i * numbers_per_process + (i < remaining ? i : remaining)); // 
            sprintf(count_str, "%d", count);
            sprintf(ith, "%d", i);

            execl("./child", "./child", shmid_str, start_index_str, count_str, filename, ith,NULL);
            perror("execl"); 
            exit(1);
        }
    }


    for(int i=0; i<num_processes; i++) wait(NULL);


    child_data *shm_ptr = (child_data*) shmat(shmid, NULL, 0);
    if(shm_ptr == (void*) -1) { perror("shmat"); exit(1); }

    long global_sum = 0.0;
    int global_max = -2147483648;
    long global_count = 0;

    for(int i=0; i<num_processes; i++) {
        global_sum += shm_ptr[i].local_sum;
        if(shm_ptr[i].local_max > global_max) 
            global_max = shm_ptr[i].local_max;
        global_count += shm_ptr[i].local_count;
    }

    double global_avg = (double) global_sum / global_count;
    

    shmdt(shm_ptr);
    shmctl(shmid, IPC_RMID, NULL);

    gettimeofday(&end_time, NULL);
    double elapsed = (end_time.tv_sec - start_time.tv_sec) + (end_time.tv_usec - start_time.tv_usec)/1000000.0;

        
    printf("=== Final Results ===\n");
    printf("Total Numbers: %ld\n", global_count);
    printf("Global Average: %.4lf\n", global_avg);
    printf("Global Maximum: %d\n", global_max);
    printf("Execution Time: %.6lf seconds\n", elapsed);
    printf("=====================\n");

    return 0;
}
