from locust import FastHttpUser, task, between, TaskSet
from locust import events
from locust.runners import MasterRunner, WorkerRunner

class UserBehavior(FastHttpUser):
    @task
    def get(self):
        self.client.get(f'/article/1')
    # @task
    # def getAll(self):
    #     self.client.get(f'/article/all?title=matched')