# json-parser

## Introduction
A very basic JSON parser.
Written with inspiration from: https://codingchallenges.fyi/challenges/challenge-json-parser

## Build status
[![Java CI with Gradle](https://github.com/Kore-rep/json-parser/actions/workflows/gradle.yml/badge.svg)](https://github.com/Kore-rep/json-parser/actions/workflows/gradle.yml)

## Features
Capable of parsing complex, nested objects and arrays, and whitespace combinations. 

## Issues
Cannot parse certain complex number types, and struggles with escape characters in certain situations.

For example will error on `"E": 1.234567890E+34` or `"backslash": "\\"` but can parse `"quote": "\""`.


