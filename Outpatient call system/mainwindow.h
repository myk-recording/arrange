#ifndef MAINWINDOW_H
#define MAINWINDOW_H

#include <QMainWindow>

#include <QDebug>
#include <QSqlDatabase>
#include <QSqlDriver>
#include <QSqlQuery>
#include <QDateTime>
#include <QSqlError>
#include <QSqlRecord>
#include <QSqlResult>
#include <QTextCodec>

namespace Ui {
class MainWindow;
}

class MainWindow : public QMainWindow
{
    Q_OBJECT

public:
    explicit MainWindow(QWidget *parent = 0);
    ~MainWindow();

    void insert_register(QString name, int age, QString sex, QString idcare, int department);
    void select_register(int department);
    void select_department_register(int department);
    void select_all_register();
    void count_register();

    void insert_doctor(QString name,QString passward,int department);
    int select_doctor(QString name,QString passward);

private:
    Ui::MainWindow *ui;

    QSqlDatabase db;

};

#endif // MAINWINDOW_H
