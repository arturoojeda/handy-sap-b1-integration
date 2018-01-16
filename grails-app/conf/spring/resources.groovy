// Place your Spring DSL code here
beans = {
    dbErp(groovy.sql.Sql, ref('dataSource_erp'))
}
