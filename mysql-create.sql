
    create table author (
       id bigint not null auto_increment,
        created datetime,
        modified datetime,
        name varchar(255),
        sequence bigint,
        book_id bigint not null,
        primary key (id)
    ) engine=InnoDB

    create table book (
       id bigint not null,
        created datetime,
        description varchar(65000),
        modified datetime,
        sequence bigint,
        title varchar(255),
        primary key (id)
    ) engine=InnoDB

    create table hibernate_sequence (
       next_val bigint
    ) engine=InnoDB

    insert into hibernate_sequence values ( 1 )

    alter table author 
       add constraint FKqi5nll4mal57ohohlv5g0qlv2 
       foreign key (book_id) 
       references book (id)

    create table author (
       id bigint not null auto_increment,
        created datetime,
        modified datetime,
        name varchar(255),
        sequence bigint,
        book_id bigint not null,
        primary key (id)
    ) engine=InnoDB

    create table book (
       id bigint not null,
        created datetime,
        description varchar(65000),
        modified datetime,
        sequence bigint,
        title varchar(255),
        primary key (id)
    ) engine=InnoDB

    create table hibernate_sequence (
       next_val bigint
    ) engine=InnoDB

    insert into hibernate_sequence values ( 1 )

    alter table author 
       add constraint FKqi5nll4mal57ohohlv5g0qlv2 
       foreign key (book_id) 
       references book (id)

    create table author (
       id bigint not null auto_increment,
        created datetime,
        modified datetime,
        name varchar(255),
        sequence bigint,
        book_id bigint not null,
        primary key (id)
    ) engine=InnoDB

    create table book (
       id bigint not null,
        created datetime,
        description varchar(65000),
        modified datetime,
        sequence bigint,
        title varchar(255),
        primary key (id)
    ) engine=InnoDB

    create table hibernate_sequence (
       next_val bigint
    ) engine=InnoDB

    insert into hibernate_sequence values ( 1 )

    alter table author 
       add constraint FKqi5nll4mal57ohohlv5g0qlv2 
       foreign key (book_id) 
       references book (id)

    create table author (
       id bigint not null auto_increment,
        created datetime,
        modified datetime,
        name varchar(255),
        sequence bigint,
        book_id bigint not null,
        primary key (id)
    ) engine=InnoDB

    create table book (
       id bigint not null,
        created datetime,
        description varchar(65000),
        modified datetime,
        sequence bigint,
        title varchar(255),
        primary key (id)
    ) engine=InnoDB

    create table hibernate_sequence (
       next_val bigint
    ) engine=InnoDB

    insert into hibernate_sequence values ( 1 )

    alter table author 
       add constraint FKqi5nll4mal57ohohlv5g0qlv2 
       foreign key (book_id) 
       references book (id)

    create table author (
       id bigint not null auto_increment,
        created datetime,
        modified datetime,
        name varchar(255),
        sequence bigint,
        book_id bigint not null,
        primary key (id)
    ) engine=InnoDB

    create table book (
       id bigint not null,
        created datetime,
        description varchar(65000),
        modified datetime,
        sequence bigint,
        title varchar(255),
        primary key (id)
    ) engine=InnoDB

    create table hibernate_sequence (
       next_val bigint
    ) engine=InnoDB

    insert into hibernate_sequence values ( 1 )

    alter table author 
       add constraint FKqi5nll4mal57ohohlv5g0qlv2 
       foreign key (book_id) 
       references book (id)
