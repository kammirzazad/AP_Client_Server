#-------------------------------------------------
#
# Project created by QtCreator 2014-05-10T11:02:20
#
#-------------------------------------------------

QT       += core

QT       -= gui

QT       += network

TARGET = AP
CONFIG   += console
CONFIG   -= app_bundle

TEMPLATE = app


SOURCES += main.cpp \
    client.cpp \
    client_proxy.cpp

HEADERS += \
    client.h \
    ../client_proxy.h

QMAKE_CXXFLAGS += -std=c++0x
