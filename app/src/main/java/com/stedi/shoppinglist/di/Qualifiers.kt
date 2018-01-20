package com.stedi.shoppinglist.di

import java.lang.annotation.Documented
import java.lang.annotation.Retention
import java.lang.annotation.RetentionPolicy
import javax.inject.Qualifier

@Qualifier
@Documented
@Retention(RetentionPolicy.RUNTIME)
annotation class AppContext

@Qualifier
@Documented
@Retention(RetentionPolicy.RUNTIME)
annotation class DefaultScheduler

@Qualifier
@Documented
@Retention(RetentionPolicy.RUNTIME)
annotation class UiScheduler