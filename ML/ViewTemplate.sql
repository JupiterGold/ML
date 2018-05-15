create view V@P1@@P2@_@AA@ ( drawon, @P1@, @P2@, N ) as
select eventon, @P1@, @P2@, @AA@ from PlanetPos where 1 in ( select count(*) from SGToto where N = @AA@ and eventOn = drawon )
union
select eventon, @P1@, @P2@, 0 from PlanetPos where eventon < ( select max(drawon) from SGToto ) and 0 in ( select count(*) from SGToto where N = @AA@ and eventOn = drawon ) ;

