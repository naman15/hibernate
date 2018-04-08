package hibernate.client;

import java.util.Iterator;
import java.util.List;

import javax.persistence.TypedQuery;
import javax.persistence.criteria.CompoundSelection;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Expression;
import javax.persistence.criteria.ParameterExpression;
import javax.persistence.criteria.Root;

import org.hibernate.Criteria;
import org.hibernate.HibernateException;
import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.hibernate.cfg.Configuration;

import hibernate.model.Employee;

public class ManageEmployee {
   private static SessionFactory factory; 
   public static void main(String[] args) {
      
      try {
         factory = new Configuration().configure().buildSessionFactory();
      } catch (Throwable ex) { 
         System.err.println("Failed to create sessionFactory object." + ex);
         throw new ExceptionInInitializerError(ex); 
      }
      
      ManageEmployee ME = new ManageEmployee();

      ME.deleteEmployees();
      /* Add few employee records in database */
      Integer empID1 = ME.addEmployee(1,"Zara", 10);
      Integer empID2 = ME.addEmployee(2,"Daisy", 50);
      Integer empID3 = ME.addEmployee(3,"John", 10);

      /* List down all the employees */
      System.out.println("=================");
      ME.listEmployees();

      /* Update employee's records */
      ME.updateEmployee(empID1, 50);

      /* Delete an employee from the database */
      ME.deleteEmployee(empID2);
      
      Integer empID4 = ME.addEmployee(4,"Johny", 10);

      /* List down new list of the employees */
      System.out.println("=================");
      ME.listEmployees();
      
      /* List Total Salary sum*/
      System.out.println("=================");
      ME.printSumOfEmployeeSalaries();
   }
   
   private void printSumOfEmployeeSalaries() 
   {
	   Session session=factory.openSession();
	   Transaction tx=null;
	   try {
	         tx = session.beginTransaction();
	         CriteriaBuilder criteriaBuilder = session.getCriteriaBuilder();
	         //We want Integer result
	         CriteriaQuery<Long> cr=criteriaBuilder.createQuery(Long.class);
	         //The root does not need to match the type of the result of the query!
	         Root<Employee> root=cr.from(Employee.class);
	         CompoundSelection<Long> projection = criteriaBuilder.construct(Long.class, criteriaBuilder.sum(root.<Long>get("salary")));
	         cr.select(projection);
	         System.out.println(session.createQuery(cr).getSingleResult());
	   }
	   catch(HibernateException e) 
	   {
		   if(tx!=null)tx.rollback();
		   e.printStackTrace();
	   }
	   finally 
	   {
		   session.close();
	   }
   }

private void deleteEmployees() 
   {
	   Session session=factory.openSession();
	   Transaction tx=null;
	   try {
	         tx = session.beginTransaction();
	         String hql="Delete from Employee";
	         TypedQuery<Employee> query=session.createQuery(hql);
	         query.executeUpdate();
	         
	   }
	   catch(HibernateException e) 
	   {
		   if(tx!=null)tx.rollback();
		   e.printStackTrace();
	   }
	   finally 
	   {
		   session.close();
	   }
   }

/* Method to CREATE an employee in the database */
   public Integer addEmployee(int id, String name, int salary){
      Session session = factory.openSession();
      Transaction tx = null;
      Integer employeeID = null;
      
      try {
         tx = session.beginTransaction();
         Employee employee = new Employee(id, name, salary);
         employeeID = (Integer) session.save(employee); 
         tx.commit();
      } catch (HibernateException e) {
         if (tx!=null) tx.rollback();
         e.printStackTrace(); 
      } finally {
         session.close(); 
      }
      return employeeID;
   }
   
   /* Method to  READ all the employees */
   public void listEmployees( ){
      Session session = factory.openSession();
      Transaction tx = null;
      
      try {
         tx = session.beginTransaction();
         List employees = session.createQuery("FROM Employee").list(); 
         for (Iterator iterator = employees.iterator(); iterator.hasNext();){
            Employee employee = (Employee) iterator.next(); 
            System.out.print("First Name: " + employee.getName()); 
            System.out.println("  Salary: " + employee.getSalary()); 
         }
         tx.commit();
      } catch (HibernateException e) {
         if (tx!=null) tx.rollback();
         e.printStackTrace(); 
      } finally {
         session.close(); 
      }
   }
   
   /* Method to UPDATE salary for an employee */
   public void updateEmployee(Integer EmployeeID, int salary ){
      Session session = factory.openSession();
      Transaction tx = null;
      
      try {
         tx = session.beginTransaction();
         Employee employee = (Employee)session.get(Employee.class, EmployeeID); 
         employee.setSalary( salary );
		 session.update(employee); 
         tx.commit();
      } catch (HibernateException e) {
         if (tx!=null) tx.rollback();
         e.printStackTrace(); 
      } finally {
         session.close(); 
      }
   }
   
   /* Method to DELETE an employee from the records */
   public void deleteEmployee(Integer EmployeeID){
      Session session = factory.openSession();
      Transaction tx = null;
      
      try {
         tx = session.beginTransaction();
         Employee employee = (Employee)session.get(Employee.class, EmployeeID); 
         session.delete(employee); 
         tx.commit();
      } catch (HibernateException e) {
         if (tx!=null) tx.rollback();
         e.printStackTrace(); 
      } finally {
         session.close(); 
      }
   }
}
