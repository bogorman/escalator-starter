export FLYWAY_PASSWORD=<CHANGE THIS>
export FLYWAY_URL="jdbc:postgresql://localhost:5432/escalatorstarter?user=postgres" 
flyway -locations="filesystem:./" info