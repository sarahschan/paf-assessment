drop database if exists movies;
create database if not exists movies;
use movies;

create table imdb(
    imdb_id varchar(16) not null,
    vote_average float default 0.0 not null,
    vote_count int default 0 not null,
    release_date date not null,
    revenue decimal(15,2) default 1000000 not null,
    budget decimal(15,2) default 1000000 not null,
    runtime int default 90 not null,
    constraint pk_imdb_id primary key (imdb_id)
);