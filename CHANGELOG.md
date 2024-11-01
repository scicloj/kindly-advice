# Change Log
All notable changes to this project will be documented in this file. This change log follows the conventions of [keepachangelog.com](http://keepachangelog.com/).

## [1-beta12] - 2024-10-31
- added `default-advisors` back in the API namespace

## [1-beta11] - 2024-10-27
- expanded the set of cases recognized as `:kind/emmy-viewers`

## [1-beta10] - 2024-10-22
- updated Kindly version
- added automatic inference for `:kind/emmy-viewers`

## [1-beta9] - 2024-10-15
- updated Kindly version

## [1-beta8] - 2024-09-10
- updated Kindly to version 4-beta11 (deep-merge fix) 
- bugfix: merging all relevant options

## [1-beta7] - 2024-09-10
- updated Kindly to version 4-beta8 (moved options to ns form)

## [1-beta6] - 2024-09-09
- changed deep-merge impl

## [1-beta5] - 2024-09-09
- fixed name conflict

## [1-beta4] - 2024-09-08
- cleaned up test code

## [1-beta3] - 2024-09-08
- when reading an ns form, mutate kindly options (PR #5)

## [1-beta2] - 2024-09-08
- added option capturing using `kindly/*options*` (PR #4)

## [1-beta1] - 2024-03-29
(just renaming the previous release as Beta) 

## [1-alpha7] - 2024-01-14
- exposing `default-advisors` in the API

## [1-alpha6] - 2024-01-11
- updated deps
- recognizing `:kind/smile-model` in some situations

## [1-alpha5] - 2023-12-01
- fixed add-advisor! -- prioritizing the new advice first

## [1-alpha4] - 2023-10-25
- fixes and tests annotating kinds #2

## [1-alpha3] - 2023-09-08
- API cleanup
- adapting to Kindly version 4-alpha3

## [1-alpha2] - 2023-08-31
- fixed the predicate for datasets (typo)
- updated deps

## [1-alpha1] - 2023-08-31
- initial draft extracted out of the kindly v4 draft
