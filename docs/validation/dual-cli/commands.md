# 双 CLI 逐指令验收矩阵

本地字段/argv/mock、安装态 help/dry-run 和真实业务证据分开记录。没有真实成功证据的指令不标记完成。

| CLI | 指令 | SDK | 本地状态 | 安装态 | 真实状态 |
|---|---|---|---|---|---|
| dreamina-canvas | `model` | `DreaminaCanvasModelRequest` | regression-passed | help-schema-verified | verified |
| dreamina-canvas | `model list` | `DreaminaCanvasModelListRequest` | regression-passed | help-schema-verified | verified |
| dreamina-canvas | `model find` | `DreaminaCanvasModelFindRequest` | regression-passed | help-schema-verified | verified |
| dreamina-canvas | `voice list` | `DreaminaCanvasVoiceListRequest` | regression-passed | help-schema-verified | verified |
| dreamina-canvas | `node create image` | `DreaminaCanvasNodeCreateImageRequest` | regression-passed | dry-run-executed | verified |
| dreamina-canvas | `node create video` | `DreaminaCanvasNodeCreateVideoRequest` | regression-passed | dry-run-executed | verified |
| dreamina-canvas | `node create audio` | `DreaminaCanvasNodeCreateAudioRequest` | regression-passed | dry-run-executed | verified |
| dreamina-canvas | `node create element` | `DreaminaCanvasNodeCreateElementRequest` | regression-passed | dry-run-executed | verified |
| dreamina-canvas | `node create text` | `DreaminaCanvasNodeCreateTextRequest` | regression-passed | dry-run-executed | verified |
| dreamina-canvas | `node create timeline` | `DreaminaCanvasNodeCreateTimelineRequest` | regression-passed | dry-run-executed | verified |
| dreamina-canvas | `node edit image` | `DreaminaCanvasNodeEditImageRequest` | regression-passed | dry-run-executed | verified |
| dreamina-canvas | `node edit video` | `DreaminaCanvasNodeEditVideoRequest` | regression-passed | dry-run-executed | verified |
| dreamina-canvas | `node edit audio` | `DreaminaCanvasNodeEditAudioRequest` | regression-passed | dry-run-executed | verified |
| dreamina-canvas | `node edit element` | `DreaminaCanvasNodeEditElementRequest` | regression-passed | dry-run-executed | verified |
| dreamina-canvas | `node edit text` | `DreaminaCanvasNodeEditTextRequest` | regression-passed | dry-run-executed | verified |
| dreamina-canvas | `node edit timeline` | `DreaminaCanvasNodeEditTimelineRequest` | regression-passed | dry-run-executed | verified |
| dreamina-canvas | `node upscale image` | `DreaminaCanvasNodeUpscaleImageRequest` | regression-passed | dry-run-executed | verified |
| dreamina-canvas | `node upscale video` | `DreaminaCanvasNodeUpscaleVideoRequest` | regression-passed | dry-run-executed | verified |
| dreamina-canvas | `node cutout image` | `DreaminaCanvasNodeCutoutImageRequest` | regression-passed | dry-run-executed | verified |
| dreamina-canvas | `node interpolate video` | `DreaminaCanvasNodeInterpolateVideoRequest` | regression-passed | dry-run-executed | verified |
| dreamina-canvas | `node promote video-sample` | `DreaminaCanvasNodePromoteVideoSampleRequest` | regression-passed | dry-run-executed | verified |
| dreamina-canvas | `node delete` | `DreaminaCanvasNodeDeleteRequest` | regression-passed | help-schema-verified | verified |
| dreamina-canvas | `node delete confirm` | `DreaminaCanvasNodeDeleteConfirmRequest` | regression-passed | help-schema-verified | verified |
| dreamina-canvas | `node delete status` | `DreaminaCanvasNodeDeleteStatusRequest` | regression-passed | help-schema-verified | verified |
| dreamina-canvas | `node show` | `DreaminaCanvasNodeShowRequest` | regression-passed | help-schema-verified | verified |
| dreamina-canvas | `node find` | `DreaminaCanvasNodeFindRequest` | regression-passed | help-schema-verified | verified |
| dreamina-canvas | `node quote` | `DreaminaCanvasNodeQuoteRequest` | regression-passed | help-schema-verified | verified |
| dreamina-canvas | `node confirm` | `DreaminaCanvasNodeConfirmRequest` | regression-passed | help-schema-verified | verified |
| dreamina-canvas | `node run` | `DreaminaCanvasNodeRunRequest` | regression-passed | help-schema-verified | verified |
| dreamina-canvas | `operation status` | `DreaminaCanvasOperationStatusRequest` | regression-passed | help-schema-verified | verified |
| dreamina-canvas | `operation wait` | `DreaminaCanvasOperationWaitRequest` | regression-passed | help-schema-verified | verified |
| dreamina-canvas | `resource download` | `DreaminaCanvasResourceDownloadRequest` | regression-passed | help-schema-verified | verified |
| dreamina-canvas | `resource get` | `DreaminaCanvasResourceGetRequest` | regression-passed | help-schema-verified | verified |
| dreamina-canvas | `resource upload` | `DreaminaCanvasResourceUploadRequest` | regression-passed | help-schema-verified | verified |
| dreamina-canvas | `canvas create` | `DreaminaCanvasCreateRequest` | regression-passed | help-schema-verified | verified |
| dreamina-canvas | `canvas ls` | `DreaminaCanvasLsRequest` | regression-passed | help-schema-verified | verified |
| dreamina-canvas | `canvas use` | `DreaminaCanvasUseRequest` | regression-passed | help-schema-verified | verified |
| dreamina-canvas | `canvas current` | `DreaminaCanvasCurrentRequest` | regression-passed | help-schema-verified | verified |
| dreamina-canvas | `auth account` | `DreaminaCanvasAuthAccountRequest` | regression-passed | help-schema-verified | verified |
| dreamina-canvas | `auth login` | `DreaminaCanvasAuthLoginRequest` | regression-passed | help-schema-verified | verified |
| dreamina-canvas | `auth logout` | `DreaminaCanvasAuthLogoutRequest` | regression-passed | help-schema-verified | verified |
| dreamina-canvas | `auth status` | `DreaminaCanvasAuthStatusRequest` | regression-passed | help-schema-verified | verified |
| dreamina-canvas | `auth wait` | `DreaminaCanvasAuthWaitRequest` | regression-passed | help-schema-verified | verified |
| dreamina-canvas | `auth refresh` | `DreaminaCanvasAuthRefreshRequest` | regression-passed | help-schema-verified | verified |
| dreamina-canvas | `schema` | `DreaminaCanvasSchemaRequest` | regression-passed | helper-executed | verified |
| dreamina-canvas | `version` | `DreaminaCanvasVersionRequest` | regression-passed | helper-executed | verified |
| dreamina-canvas | `team ls` | `DreaminaCanvasTeamLsRequest` | regression-passed | help-schema-verified | verified |
| dreamina-canvas | `space use` | `DreaminaCanvasSpaceUseRequest` | regression-passed | help-schema-verified | verified-with-scenario-gaps |
| dreamina-canvas | `space current` | `DreaminaCanvasSpaceCurrentRequest` | regression-passed | help-schema-verified | verified |
| dreamina-canvas | `director create` | `DreaminaCanvasDirectorCreateRequest` | regression-passed | help-schema-verified | recovered-success |
| dreamina-canvas | `director pull` | `DreaminaCanvasDirectorPullRequest` | regression-passed | help-schema-verified | verified |
| dreamina-canvas | `director show` | `DreaminaCanvasDirectorShowRequest` | regression-passed | help-schema-verified | verified |
| dreamina-canvas | `director apply` | `DreaminaCanvasDirectorApplyRequest` | regression-passed | help-schema-verified | verified |
| dreamina-canvas | `director push` | `DreaminaCanvasDirectorPushRequest` | regression-passed | help-schema-verified | verified |
| dreamina-canvas | `director catalog` | `DreaminaCanvasDirectorCatalogRequest` | regression-passed | help-schema-verified | verified |
| dreamina-canvas | `director export` | `DreaminaCanvasDirectorExportRequest` | regression-passed | help-schema-verified | verified |
| dreamina-canvas | `director freeze` | `DreaminaCanvasDirectorFreezeRequest` | regression-passed | help-schema-verified | verified |
| dreamina-canvas | `director preview` | `DreaminaCanvasDirectorPreviewRequest` | regression-passed | help-schema-verified | verified |
| dreamina-canvas | `director resume-export` | `DreaminaCanvasDirectorResumeExportRequest` | regression-passed | help-schema-verified | verified |
| dreamina-canvas | `schema director` | `DreaminaCanvasSchemaDirectorRequest` | regression-passed | help-schema-verified | verified |
| dreamina-canvas | `help` | `help` | regression-passed | helper-executed | verified |
| dreamina-canvas | `completion bash` | `completionBash` | regression-passed | helper-executed | verified |
| dreamina-canvas | `completion zsh` | `completionZsh` | regression-passed | helper-executed | verified |
| dreamina-canvas | `completion fish` | `completionFish` | regression-passed | helper-executed | verified |
| dreamina-canvas | `completion powershell` | `completionPowershell` | regression-passed | helper-executed | verified |
| dreamina | `version` | `versionInfo` | regression-passed | not-recorded | verified |
| dreamina | `help` | `helpInfo` | regression-passed | not-recorded | verified |
| dreamina | `login` | `loginInfo` | regression-passed | not-recorded | verified |
| dreamina | `login checklogin` | `checkLoginInfo` | regression-passed | not-recorded | verified |
| dreamina | `logout` | `logoutInfo` | regression-passed | not-recorded | verified |
| dreamina | `relogin` | `reloginInfo` | regression-passed | not-recorded | verified |
| dreamina | `user_credit` | `userCreditInfo` | regression-passed | not-recorded | verified |
| dreamina | `list_task` | `listTaskInfo` | regression-passed | not-recorded | verified |
| dreamina | `query_result` | `queryResultInfo` | regression-passed | not-recorded | verified |
| dreamina | `session` | `sessionInfo` | regression-passed | not-recorded | verified |
| dreamina | `session create` | `sessionCreateInfo` | regression-passed | not-recorded | verified |
| dreamina | `session list` | `sessionListInfo` | regression-passed | not-recorded | verified |
| dreamina | `session search` | `sessionSearchInfo` | regression-passed | not-recorded | verified |
| dreamina | `session rename` | `sessionRenameInfo` | regression-passed | not-recorded | verified |
| dreamina | `session delete` | `sessionDelete` | regression-passed | not-recorded | verified |
| dreamina | `text2image` | `text2ImageSubmit` | regression-passed | not-recorded | verified |
| dreamina | `image2image` | `image2ImageSubmit` | regression-passed | not-recorded | verified |
| dreamina | `text2video` | `text2VideoSubmit` | regression-passed | not-recorded | verified |
| dreamina | `image2video` | `image2VideoSubmit` | regression-passed | not-recorded | verified |
| dreamina | `frames2video` | `frames2VideoSubmit` | regression-passed | not-recorded | verified |
| dreamina | `multiframe2video` | `multiframe2VideoSubmit` | regression-passed | not-recorded | verified |
| dreamina | `multimodal2video` | `multimodal2VideoSubmit` | regression-passed | not-recorded | verified |
| dreamina | `image_upscale` | `imageUpscaleSubmit` | regression-passed | not-recorded | verified |
