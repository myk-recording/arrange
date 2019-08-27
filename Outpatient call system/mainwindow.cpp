#include "mainwindow.h"
#include "ui_mainwindow.h"

MainWindow::MainWindow(QWidget *parent) :
    QMainWindow(parent),
    ui(new Ui::MainWindow)
{
    ui->setupUi(this);

    QTextCodec *codec = QTextCodec::codecForName("UTF-8");
    QTextCodec::setCodecForLocale(codec);

    QString create_register_sql = "CREATE TABLE IF NOT EXISTS "
           "register_tb3(id INTEGER PRIMARY KEY autoincrement, date VARCHAR(255) "
           ", name VARCHAR(255), age INT, sex VARCHAR(5), idcare VARCHAR(25), "
           "department INT, remark VARCHAR(10));";

    QString create_doctor_sql = "CREATE TABLE IF NOT EXISTS "
           "doctor_tb(id INTEGER PRIMARY KEY autoincrement"
           ", name VARCHAR(255), passward VARCHAR(255), "
           "department INT);";

    QString drop_register = "DROP TABLE register_tb3";
    QString drop_doctor = "DROP TABLE doctor_tb";

    db = QSqlDatabase::addDatabase("QSQLITE");
    db.setDatabaseName("./outpatient.db");
    if(!db.open())
    {
        qDebug() << "db.open()";

    }

    QSqlQuery db_operator(db);

    //* 如果不存在表格则创建
    if(!db_operator.exec(create_register_sql))
    {
        qDebug() << "!db_operator.exec(create_register_sql)";
    }
    db.commit();

    //* 如果不存在表格则创建
    if(!db_operator.exec(create_doctor_sql))
    {
        qDebug() << "!db_operator.exec(create_doctor_sql)";
    }

    db.commit();

    QString name = "王小强";

    insert_register(name,66,"男","012345678901234567",3);

    insert_register("李明",16,"男","012345678901234567",3);

    select_register(3);

    select_all_register();

    select_department_register(3);

    count_register();

    insert_doctor("菜单上","opqjcef",3);

    int department = select_doctor("菜单上","opqjcef");

    qDebug("doctor_department:%d",department);

    if(!db_operator.exec(drop_register))
    {
        qDebug() << "!db_operator.exec(drop_register)";
    }
    db.commit();
    if(!db_operator.exec(drop_doctor))
    {
        qDebug() << "!db_operator.exec(drop_doctor)";
    }
    db.commit();


}

MainWindow::~MainWindow()
{
    delete ui;
}

//插入挂号数据
void MainWindow::insert_register(QString name, int age, QString sex, QString idcare, int department)
{
    QString insert_register_sql = "INSERT INTO register_tb3"
           "(id, date, name, age, sex, idcare, department, remark) "
           "VALUES(null,'%1','%2',%3,'%4','%5',%6,'等待');";
    QString error_tip  = "%1:[%2]%3";
    QString date = QDateTime::currentDateTime().toString("yyyy-MM-dd");

    qDebug("name:%s",name.toStdString().data());

    QSqlQuery db_operator(db);
    if(!db_operator.exec(insert_register_sql.arg
        (date).arg(name).arg(age).arg(sex).arg(idcare).arg(department)))
    {
        qDebug() << "!db_operator.exec(insert_sql.arg(QDateTime::currentDateTime().toString()).arg(name)).arg(department)";
        qDebug() << error_tip.arg("insert failed").arg(db_operator.lastError().type()).arg(db_operator.lastError().text());
    }
    db.commit();
}

//叫号查询
void MainWindow::select_register(int department)
{
    QString select_register_sql = "SELECT date, name, id "
           "FROM register_tb3 WHERE department = %1"
           " AND remark = '等待';";
    QString update_register_sql = "UPDATE register_tb3 "
           "SET remark = '已叫号' WHERE date = '%1' AND "
           "name = '%2';";

    QSqlQuery db_operator(db);
    if(!db_operator.exec(select_register_sql.arg(department)))
    {
        qDebug() << "!db_operator.exec(select_register_sql)";
    }
    else
    {
        QString name1;
        QString date;
        int id;
        QString name2;
        db_operator.next();

        date = db_operator.value(0).toString();
        name1 = db_operator.value(1).toString();
        id = db_operator.value(2).toInt();

        qDebug("Date:%s",date.toStdString().data());
        qDebug("name1:%s",name1.toStdString().data());
        qDebug("id:%d",id);

        //下一个病人
        if(db_operator.next())
        {
            name2 = db_operator.value(1).toString();
            qDebug("name2:%s",name2.toStdString().data());
            if(!db_operator.exec(update_register_sql.arg(date).arg(name1)))
            {
                qDebug() << "!db_operator.exec(update_register_sql.arg(date).arg(name1))";
            }
            db.commit();
        }
    }

}

//查询该科室挂号数据
void MainWindow::select_department_register(int department)
{
    QString select_department_register_sql = "SELECT date, name, age, sex, idcare, department, remark "
           "FROM register_tb3 WHERE department = %1;";

    QSqlQuery db_operator(db);
    if(!db_operator.exec(select_department_register_sql.arg(department)))
    {
        qDebug() << "!db_operator.exec(select_department_register_sql.arg(department))";
    }
    else
    {
        while(db_operator.next())
        {
            QString date = db_operator.value(0).toString();
            QString name = db_operator.value(1).toString();
            int age = db_operator.value(2).toInt();
            QString sex = db_operator.value(3).toString();
            QString idcare = db_operator.value(4).toString();
            int department = db_operator.value(5).toInt();
            QString remark = db_operator.value(6).toString();

//            qDebug("date:%s",date.toStdString().data());
//            qDebug("name:%s",name.toStdString().data());
//            qDebug("age:%d",age);
//            qDebug("sex:%s",sex.toStdString().data());
//            qDebug("idcare:%s",idcare.toStdString().data());
//            qDebug("department:%d",department);
//            qDebug("remark:%s",remark.toStdString().data());
        }
    }
}

//查询所有挂号数据
void MainWindow::select_all_register()
{
    QString select_all_register_sql = "SELECT date, name, age, sex, idcare, department, remark "
           "FROM register_tb3;";

    QSqlQuery db_operator(db);

    if(!db_operator.exec(select_all_register_sql))
    {
        qDebug() << "!db_operator.exec(select_all_register_sql))";
    }
    else
    {
        while(db_operator.next())
        {
            QString date = db_operator.value(0).toString();
            QString name = db_operator.value(1).toString();
            int age = db_operator.value(2).toInt();
            QString sex = db_operator.value(3).toString();
            QString idcare = db_operator.value(4).toString();
            int department = db_operator.value(5).toInt();
            QString remark = db_operator.value(6).toString();

//            qDebug("date:%s",date.toStdString().data());
//            qDebug("name:%s",name.toStdString().data());
//            qDebug("age:%d",age);
//            qDebug("sex:%s",sex.toStdString().data());
//            qDebug("idcare:%s",idcare.toStdString().data());
//            qDebug("department:%d",department);
//            qDebug("remark:%s",remark.toStdString().data());
        }
    }
}

//每日挂号数据查询
void MainWindow::count_register()
{
    QString count_register_sql = "SELECT date, COUNT(id) FROM register_tb3 "
                                 "GROUP BY date;";
    QString select_date_register_sql = "SELECT date, name, age, sex, idcare, department, remark "
           "FROM register_tb3 WHERE date = '%1';";

    QStringList date_list;
    QStringList count_list;
    QSqlQuery db_operator(db);

    if(!db_operator.exec(count_register_sql))
    {
        qDebug() << "!db_operator.exec(count_register_sql)";
    }
    else
    {
        while(db_operator.next())
        {
            date_list.append(db_operator.value(0).toString());
            count_list.append(db_operator.value(1).toString());
        }
    }

    for(int i;i < date_list.size();i++)
    {
        qDebug("date_list:%s",date_list.at(i).toStdString().data());
        qDebug("count_list:%s",count_list.at(i).toStdString().data());
        if(!db_operator.exec(select_date_register_sql.arg(date_list.at(i))))
        {
            qDebug() << "!db_operator.exec(select_date_register_sql)";
        }
        else
        {
            while(db_operator.next())
            {
                QString date = db_operator.value(0).toString();
                QString name = db_operator.value(1).toString();
                int age = db_operator.value(2).toInt();
                QString sex = db_operator.value(3).toString();
                QString idcare = db_operator.value(4).toString();
                int department = db_operator.value(5).toInt();
                QString remark = db_operator.value(6).toString();

                qDebug("date:%s",date.toStdString().data());
                qDebug("name:%s",name.toStdString().data());
                qDebug("age:%d",age);
                qDebug("sex:%s",sex.toStdString().data());
                qDebug("idcare:%s",idcare.toStdString().data());
                qDebug("department:%d",department);
                qDebug("remark:%s",remark.toStdString().data());
            }
        }
    }
}

//插入医生数据
void MainWindow::insert_doctor(QString name, QString passward, int department)
{
    QString insert_doctor_sql = "INSERT INTO doctor_tb"
           "(id, name, passward, department) "
           "VALUES(null,'%1','%2',%3);";
    QString error_tip  = "%1:[%2]%3";

    QSqlQuery db_operator(db);
    if(!db_operator.exec(insert_doctor_sql.arg
        (name).arg(passward).arg(department)))
    {
        qDebug() << "!db_operator.exec(insert_doctor_sql.arg(name).arg(passward).arg(department)))";
        qDebug() << error_tip.arg("insert failed").arg(db_operator.lastError().type()).arg(db_operator.lastError().text());
    }
    db.commit();
}

//查询医生科室号
int MainWindow::select_doctor(QString name, QString passward)
{
    QString select_doctor_sql = "SELECT department "
           "FROM doctor_tb WHERE name = '%1' AND passward = '%2';";
    int department;
    QSqlQuery db_operator(db);
    if(!db_operator.exec(select_doctor_sql.arg(name).arg(passward)))
    {
        qDebug() << "!db_operator.exec(select_doctor_sql.arg(name).arg(passward))";
    }
    else
    {
        db_operator.next();
        department = db_operator.value(0).toInt();
    }
    return department;
}
