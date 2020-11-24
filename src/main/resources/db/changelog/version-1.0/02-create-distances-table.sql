
create table distances(
 id int not null auto_increment,
 distance double not null,
 from_City int,
 to_City int,
 primary key (id)
) engine = InnoDB

GO

alter table distances
add constraint fromCity
foreign key (from_City)
references cities(id)

GO

alter table distances
add constraint toCity
foreign key (to_City)
references cities(id)

GO


