/*
 * Copyright 2022 EPAM Systems.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package platform.qa.operators.steps;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.awaitility.Awaitility.await;
import static platform.qa.git.GerritClient.BUILD_FAILED;
import static platform.qa.git.GerritClient.BUILD_SUCCESSFUL;

import io.fabric8.kubernetes.api.model.Pod;
import lombok.Setter;
import lombok.SneakyThrows;
import lombok.extern.log4j.Log4j2;
import platform.qa.entities.Repository;
import platform.qa.entities.WaitConfiguration;
import platform.qa.git.GerritClient;
import platform.qa.git.JgitClient;
import platform.qa.git.entity.changes.ChangesDetailResponse;
import platform.qa.git.entity.changes.Messages;
import platform.qa.jenkins.JenkinsClient;
import platform.qa.jenkins.JenkinsRestClient;
import platform.qa.oc.OkdClient;
import platform.qa.orchestrator.OrchestratorClient;
import platform.qa.operators.steps.entities.FilterGerritChanges;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicReference;
import java.util.stream.Collectors;
import org.assertj.core.util.Lists;
import org.eclipse.jgit.api.Git;
import com.offbytwo.jenkins.model.Build;

/**
 * Steps to work with Jenkins / Tekton
 */
@Log4j2
public class OperatorSteps {

    private static final String CHANGE_ID = "change_id";
    private static final String PROJECT_CHANGE = "project";
    private static final String NUMBER_MERGE_REQUEST = "_number";
    private static final String FOLDER = "registry-regulations";
    private static final String JOB = "MASTER-Code-review-registry-regulations";
    private JenkinsClient jenkins;
    private JenkinsRestClient jenkinsRest;
    private OkdClient ocClient; // Deprecated: use orchestratorClient instead
    private OrchestratorClient orchestratorClient;
    private GerritClient gerrit;
    @Setter
    private WaitConfiguration waitConfiguration = WaitConfiguration
            .newBuilder()
            .setPoolIntervalTimeout(1)
            .setPoolIntervalTimeUnit(TimeUnit.MINUTES)
            .setWaitTimeout(4)
            .build();

    // New constructors using OrchestratorClient (recommended)
    public OperatorSteps(JenkinsClient jenkins, OrchestratorClient orchestratorClient, GerritClient gerrit) {
        this.jenkins = jenkins;
        this.orchestratorClient = orchestratorClient;
        this.gerrit = gerrit;
        // For backward compatibility
        if (orchestratorClient instanceof OkdClient) {
            this.ocClient = (OkdClient) orchestratorClient;
        }
    }

    public OperatorSteps(JenkinsClient jenkins, OrchestratorClient orchestratorClient, GerritClient gerrit,
            JenkinsRestClient jenkinsRest) {
        this.jenkins = jenkins;
        this.orchestratorClient = orchestratorClient;
        this.gerrit = gerrit;
        this.jenkinsRest = jenkinsRest;
        // For backward compatibility
        if (orchestratorClient instanceof OkdClient) {
            this.ocClient = (OkdClient) orchestratorClient;
        }
    }

    public OperatorSteps(OrchestratorClient orchestratorClient, GerritClient gerrit) {
        this.orchestratorClient = orchestratorClient;
        this.gerrit = gerrit;
        // For backward compatibility
        if (orchestratorClient instanceof OkdClient) {
            this.ocClient = (OkdClient) orchestratorClient;
        }
    }

    // Legacy constructors using OkdClient (deprecated but maintained for backward compatibility)
    @Deprecated
    public OperatorSteps(JenkinsClient jenkins, OkdClient ocClient, GerritClient gerrit) {
        this.jenkins = jenkins;
        this.ocClient = ocClient;
        this.orchestratorClient = ocClient; // Set both for compatibility
        this.gerrit = gerrit;
    }

    @Deprecated
    public OperatorSteps(JenkinsClient jenkins, OkdClient ocClient, GerritClient gerrit,
            JenkinsRestClient jenkinsRest) {
        this.jenkins = jenkins;
        this.ocClient = ocClient;
        this.orchestratorClient = ocClient; // Set both for compatibility
        this.gerrit = gerrit;
        this.jenkinsRest = jenkinsRest;
    }

    @Deprecated
    public OperatorSteps(OkdClient ocClient, GerritClient gerrit) {
        this.ocClient = ocClient;
        this.orchestratorClient = ocClient; // Set both for compatibility
        this.gerrit = gerrit;
    }

    /**
     * Get the orchestrator client to use (prioritizes orchestratorClient over legacy ocClient)
     * @return the orchestrator client to use
     */
    private OrchestratorClient getOrchestratorClient() {
        return orchestratorClient != null ? orchestratorClient : ocClient;
    }

    public void cleanupEnvironmentWithTekton(Boolean deleteRegulationsGerritRepository) {
        log.info("Оператор очищує середовище за допомогою pipeline 'cleanup'");
        getOrchestratorClient().runCleanUpJob(deleteRegulationsGerritRepository);
    }

    public void cleanupEnvironment() {
        log.info("Оператор очищує середовище за допомогою 'cleanup-job'");
        log.info(jenkins);

        var buildId = jenkins.startJob("cleanup-job");
        jenkins.waitJobCompletion("cleanup-job", buildId);
        jenkins.waitJobCompletion("registry-regulations", "Create-release-registry-regulations", 1);
    }

    public void cleanupTempDBs() {
        log.info("Оператор очищує тимчасові БД за допомогою 'cleanup-of-version-candidate-db'");
        log.info(jenkins);

        var buildId = jenkins.startJob("cleanup-of-version-candidate-db");
        jenkins.waitJobCompletion("cleanup-of-version-candidate-db", buildId);
    }

    public void waitForPodsToBeReady(List<String> podsToBeReady) {
        log.info("Оператор очікує готовності поди та її контейнерів: {}", podsToBeReady);
        var orchestratorClient = getOrchestratorClient();

        await()
                .pollInterval(waitConfiguration.getPoolIntervalTimeout(), waitConfiguration.getPoolIntervalTimeUnit())
                .atMost(waitConfiguration.getWaitTimeout(), waitConfiguration.getWaitTimeUnit())
                .untilAsserted(() -> {
                    List<Pod> pods = orchestratorClient.getPodList(orchestratorClient.getNamespace()).getItems();

                    var expectedPods = pods
                            .stream()
                            .filter(pod -> pod.getMetadata().getLabels().values().stream().anyMatch(podsToBeReady::contains))
                            .collect(Collectors.toList());

                    for (int i = expectedPods.size() - 1; i >= 0; i--) {
                        var podStatus = expectedPods.get(i).getStatus().getPhase();
                        if ("Running".equals(podStatus)) {
                            expectedPods.remove(i);
                        }
                    }

                    assertThat(expectedPods.size())
                            .as("Pods by lables: " + expectedPods + " is not in Running status!")
                            .isEqualTo(0);
                });
    }

    public void waitForPodToBeReady(String podLabel) {
        log.info("Оператор очікує готовності поди та її контейнерів: {}", podLabel);
        var client = ocClient;

        await()
                .pollInterval(waitConfiguration.getPoolIntervalTimeout(), waitConfiguration.getPoolIntervalTimeUnit())
                .atMost(waitConfiguration.getWaitTimeout(), waitConfiguration.getWaitTimeUnit())
                .untilAsserted(() -> {
                    List<Pod> list = client.getOsClient()
                            .pods()
                            .withLabel(podLabel)
                            .list()
                            .getItems();


                    assertThat(list.isEmpty())
                            .as("Pods for " + podLabel + " are not up")
                            .isFalse();

                    var status = list.get(0).getStatus();

                    assertThat("Running")
                            .as("Pods for " + podLabel + " are not in Running status")
                            .isEqualTo(status.getPhase());

                    assertThat(status.getContainerStatuses().get(0).getReady())
                            .as("Pods for " + podLabel + " are not up")
                            .isTrue();
                });
    }

    public Git getClonedWithHooksDataFromVCS(JgitClient jgitClient) {
        return jgitClient.cloneGerritRepositoryWithHooksLocally();
    }

    public Git getClonedDataFromVCS(JgitClient jgitClient) {
        return jgitClient.cloneGerritRepositoryLocally();
    }

    public JgitClient getInitializedJgitClient(Repository source, Repository destination, List<String> foldersToCopy) {
        log.info("Оператор загружає дані папок:{} з репозиторію: {} в : {}", foldersToCopy,
                source.getUrl(), destination.getUrl());
        return new JgitClient(destination)
                .takeTestDataFrom(source, foldersToCopy);
    }

    public void deploysChangeFromRepository(Repository source, Repository destination, List<String> foldersToCopy) {
        log.info("Оператор закидує зміни, що містяться у з репозитарію тестових даних {} {}", source, foldersToCopy);
        String changeId = getInitializedJgitClient(source, destination, foldersToCopy)
                .submitChange();
        deployChange(changeId);
    }

    public void deploysChangeFromRepository(Repository source, Repository destination) {
        log.info("Оператор закидує зміни, що містяться у з репозитарію тестових даних {}", source);
        String changeId = getInitializedJgitClient(source, destination, Lists.newArrayList())
                .submitChange();
        deployChange(changeId);
    }

    public void deployChangeToGerrit(JgitClient jgitClient, Git gerritLocal, Git gerritTestDataLocal) {
        log.info("Оператор загружає зміни з gerrit");
        String changeId = jgitClient.submitChange(gerritLocal, gerritTestDataLocal);
        deployChange(changeId);
    }

    public void deployChangeAccess(FilterGerritChanges filterGerritChanges) {
        log.info("Оператор підтверджує зміни: {}", filterGerritChanges);
        String changeId = getChangeIdByFilter(filterGerritChanges);
        gerrit.reviewRevisionOneAndSubmit(changeId);
    }

    public void verifyJenkinsStatusBuilds(String changeId, String buildUniqueContains) {
        log.info("Перевірка Jenkins статусу для Merge request з changeId: {}", changeId);
        AtomicReference<List<Messages>> messagesAtomicReference = new AtomicReference<>();
        await()
                .pollInterval(waitConfiguration.getPoolIntervalTimeout(), waitConfiguration.getPoolIntervalTimeUnit())
                .atMost(waitConfiguration.getWaitTimeout(), waitConfiguration.getWaitTimeUnit())
                .pollInSameThread()
                .untilAsserted(() -> {
                    ChangesDetailResponse changesDetailResponse = gerrit.getChangesDetailById(changeId);
                    List<Messages> allMessages = changesDetailResponse.getMessages();
                    messagesAtomicReference.set(allMessages);
                    List<Messages> messagesWithResultBuild = allMessages.stream()
                            .filter(el -> el != null && (el.getMessage().contains(BUILD_SUCCESSFUL) || el.getMessage()
                                    .contains(BUILD_FAILED))).toList();

                    assertThat(messagesWithResultBuild
                            .stream()
                            .anyMatch(el -> el != null
                                    && (el.getMessage().contains(buildUniqueContains) && el.getMessage().contains(BUILD_SUCCESSFUL)
                                    || (el.getMessage().contains(BUILD_FAILED)))))
                            .isTrue();
                });
        List<Messages> messages = messagesAtomicReference.get();
        Optional<Messages> messageWithBuildFailed =
                messages.stream().filter(el -> el != null && el.getMessage().contains(BUILD_FAILED)).findFirst();
        String resultBuilds =
                messageWithBuildFailed.map(value -> BUILD_FAILED + value.getMessage()).orElse(BUILD_SUCCESSFUL);

        assertThat(messageWithBuildFailed)
                .as(resultBuilds)
                .isEmpty();
    }

    public String getChangeIdByFilter(FilterGerritChanges filterGerritChanges) {
        return gerrit
                .getChangeInfo(filterGerritChanges.getStatus(), filterGerritChanges.getCountLastChanges()).stream()
                .filter(this::isValidChanges)
                .filter(change -> filterGerritChanges.getProject().equals(change.get(PROJECT_CHANGE).toString()))
                .filter(change -> filterGerritChanges.getNumberMergeRequests().equals(change.get(NUMBER_MERGE_REQUEST).toString()))
                .findFirst()
                .orElseThrow(() -> new IllegalStateException(
                        String.format("Not found changes in Gerrit by filter: %s ", filterGerritChanges)))
                .get(CHANGE_ID).toString();
    }

    private boolean isValidChanges(Map<String, Object> changeMap) {
        return changeMap.get(PROJECT_CHANGE) != null
                && changeMap.get(NUMBER_MERGE_REQUEST) != null
                && changeMap.get(CHANGE_ID) != null;
    }

    private void deployChange(String changeId) {
        gerrit.setWaitConfiguration(waitConfiguration);
        gerrit.reviewAndSubmitRequest(changeId);
    }


    @SneakyThrows
    public void cleanUpJenkinsJobQueue() {
        Build build;
        do {
            int buildNumber = jenkins.getLastCompletedBuildForJob(FOLDER, JOB);
            build = jenkins.getBuildForJobByNumber(FOLDER, JOB, buildNumber + 1);
            if (build != null) {
                build.Stop();
            }
        } while (build != null);
    }

    public void markChangeAsVerified(String changeId, int revision, Object verified) {
        gerrit.markChangeAsVerified(changeId, revision, verified);
    }

    public void deleteChange(String changeId) {
        gerrit.deleteChange(changeId);
    }

    public String getVerifiedStatusForChangeById(String changeId) {
        String currentVerifiedStatus;
        log.info("Очікування отримання статусу verified для change з id {}", changeId);
        await()
                .pollInterval(5, TimeUnit.SECONDS)
                .atMost(5, TimeUnit.MINUTES)
                .ignoreExceptions()
                .pollInSameThread()
                .untilAsserted(() -> assertThat(gerrit.getChangesDetailByIdAsString(changeId))
                        .as("Verified Status:")
                        .doesNotContain("Verified\":{\"values\":{\"-1\":\"Fails\",\" 0\":\"No score\"," +
                                "\"+1\":\"Verified\"}")
                        .doesNotContain("\"Verified\":{\"all\""));
        String response = gerrit.getChangesDetailByIdAsString(changeId);

        if (response.contains("Verified\":{\"rejected")) {
            currentVerifiedStatus = "failed";
        } else if (response.contains("Verified\":{\"approved")) {
            currentVerifiedStatus = "success";
        } else {
            currentVerifiedStatus = "unknown";
        }
        return currentVerifiedStatus;
    }

    public void waitForAbortedStatusForChangeById(String changeId) {
        log.info("Очікування статусу build aborted для change з id {}", changeId);
        await()
                .pollInterval(5, TimeUnit.SECONDS)
                .atMost(3, TimeUnit.MINUTES)
                .ignoreExceptions()
                .pollInSameThread()
                .untilAsserted(() -> assertThat(gerrit.getChangesDetailByIdAsString(changeId))
                        .as("Build status")
                        .contains("Build Aborted"));
    }

    public void waitForUnstableStatusForChangeById(String changeId) {
        log.info("Очікування статусу build unstable для change з id {}", changeId);
        await()
                .pollInterval(5, TimeUnit.SECONDS)
                .atMost(5, TimeUnit.MINUTES)
                .ignoreExceptions()
                .pollInSameThread()
                .untilAsserted(() -> assertThat(gerrit.getChangesDetailByIdAsString(changeId))
                        .as("Build status")
                        .contains("Build Unstable"));
    }

    public void cleanUpJenkinsBuildQueue() {
        String script = "def q = Jenkins.instance.queue;\n"
                + "q.items.findAll { it.task.name.startsWith('MASTER-Code-review') }.each { q.cancel(it.task) }";
        jenkinsRest.executeJenkinsScript(script);
    }

    public void cleanUpJenkinsBuildQueue(String pipelineName) {
        String script = "def q = Jenkins.instance.queue;\n"
                + "q.items.findAll { it.task.name.startsWith('" + pipelineName + "') }.each { q.cancel(it.task) }";
        jenkinsRest.executeJenkinsScript(script);
    }

    public void abortJenkinsBuild() {
        String script = "def jobName = \"registry-regulations/MASTER-Code-review-registry-regulations\"\n"
                + "def job = Jenkins.instance.getItemByFullName(jobName)\n"
                + "def lastBuildInProgress = null\n"
                + "job.builds.findAll { it.isBuilding() }.each { build ->\n"
                + "    if (lastBuildInProgress == null || build.number > lastBuildInProgress.number) {\n"
                + "        lastBuildInProgress = build\n"
                + "    }\n"
                + "}\n"
                + "if (lastBuildInProgress != null) {\n"
                + "    lastBuildInProgress.finish(hudson.model.Result.ABORTED, new java.io.IOException(\"Aborting "
                + "build\"))\n"
                + "} else {\n"
                + "    println(\"No builds in progress for job: ${jobName}\")\n"
                + "}";
        jenkinsRest.executeJenkinsScript(script);
    }

    public void abortJenkinsBuild(String pipelineName) {
        String script = "def jobName = \"registry-regulations/" + pipelineName + "\"\n"
                + "def job = Jenkins.instance.getItemByFullName(jobName)\n"
                + "def lastBuildInProgress = null\n"
                + "job.builds.findAll { it.isBuilding() }.each { build ->\n"
                + "    if (lastBuildInProgress == null || build.number > lastBuildInProgress.number) {\n"
                + "        lastBuildInProgress = build\n"
                + "    }\n"
                + "}\n"
                + "if (lastBuildInProgress != null) {\n"
                + "    lastBuildInProgress.finish(hudson.model.Result.ABORTED, new java.io.IOException(\"Aborting "
                + "build\"))\n"
                + "} else {\n"
                + "    println(\"No builds in progress for job: ${jobName}\")\n"
                + "}";
        jenkinsRest.executeJenkinsScript(script);
    }

    public void executeJenkinsScript(String script) {
        jenkinsRest.executeJenkinsScript(script);
    }

    public void abortCodeReviewPipelineRunsInTekton() {
        log.info("Оператор зупиняє всі код ревью пайплайни в Tekton");
        getOrchestratorClient().abortRegistryRegulationsCodeReviewPipelines();
    }

    public void abortBuildPipelineRunsInTekton() {
        log.info("Оператор зупиняє всі build пайплайни в Tekton");
        getOrchestratorClient().abortRegistryRegulationsBuildPipelines();
    }

    public void waitForLatestBuildPipelineRunInTekton() {
        log.info("Оператор чекає на завершення останнього build пайплайну в Tekton");
        getOrchestratorClient().waitForLatestRegistryRegulationsBuildPipelineToBeCompleted();
    }

    public void waitForLatestCodeReviewPipelineRunInTekton() {
        log.info("Оператор чекає на завершення останнього code review пайплайну в Tekton");
        getOrchestratorClient().waitForLatestRegistryRegulationsCodeReviewPipelineToBeCompleted();
    }

    public String runHistoryExcerptPipelineAndGetLinkToReport(String uuid, String tableName) {
        log.info("Оператор запускає history-excerptor пайплайн в Tekton і повертає посилання на отриманий звіт");
        return getOrchestratorClient().runHistoryExcerptorPipeline(uuid, tableName);
    }

    public void runRegistryRegulationsBuildPipeline() {
        log.info("Оператор запускає build пайплайн в Tekton");
        getOrchestratorClient().runRegistryRegulationsBuildPipeline();
    }

    public void runDataModelBuildPipeline() {
        log.info("Оператор запускає data model build пайплайн в Tekton");
        getOrchestratorClient().runDataModelBuildPipeline();
    }
}
