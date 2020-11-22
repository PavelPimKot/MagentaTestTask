
create table distances(
 id int not null auto_increment,
 distance double not null,
 fromCity int,
 toCity int,
 primary key (id)
) engine = InnoDB

GO

alter table distances
add constraint FROM_CITY
foreign key (fromCity)
references cities(id)

GO

alter table distances
add constraint TO_CITY
foreign key (toCity)
references cities(id)

GO


