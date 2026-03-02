package com.yuridiasns.secure_file_explorer_backend.external.manager;

import org.springframework.stereotype.Component;

import java.util.concurrent.ConcurrentHashMap;
import java.util.Collection;

@Component
public class DownloadManager {

    private final ConcurrentHashMap<String, DownloadJob> jobs = new ConcurrentHashMap<>();

    public DownloadJob createJob(String url) {

        DownloadJob job = new DownloadJob(url);

        jobs.put(job.getJobId(), job);

        return job;
    }

    public DownloadJob getJob(String jobId) {
        return jobs.get(jobId);
    }

    public Collection<DownloadJob> getAllJobs() {
        return jobs.values();
    }

    public void removeJob(String jobId) {
        jobs.remove(jobId);
    }

    public void cancelJob(String jobId) {

        DownloadJob job = jobs.get(jobId);

        if (job == null) {
            throw new RuntimeException("Download não encontrado");
        }

        job.cancel();
    }

}
