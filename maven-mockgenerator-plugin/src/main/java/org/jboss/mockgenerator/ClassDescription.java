/*
 * $Id$
 *
 * License Agreement.
 *
 * Rich Faces - Natural Ajax for Java Server Faces (JSF)
 *
 * Copyright (C) 2007 Exadel, Inc.
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License version 2.1 as published by the Free Software Foundation.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA 02110-1301  USA
 */

package org.jboss.mockgenerator;


/**
 * <p class="changed_added_4_0"></p>
 * @author asmirnov@exadel.com
 *
 */
public class ClassDescription {

        /**
          * <p class="changed_added_4_0"></p>
          */
        private static final long serialVersionUID = -846623207703750456L;


        /**
          * <p class="changed_added_4_0"></p>
          */
        private final String name;



        /**
          * <p class="changed_added_4_0">Id parameters for that class</p>
          * TODO append type parameters to key.
          */
        private String typeParameters;

        private final String fullName;

        /**
         * <p class="changed_added_4_0">
         * </p>
         * 
         * @param name
         */
        public ClassDescription(String name) {
            fullName = name;
                int i = name.indexOf('<');
                if(i>0){
                    this.name = name.substring(0,i);
                    this.typeParameters=name.substring(i);
                } else {
                    this.name = name;
                }
        }

        /**
         * <p class="changed_added_4_0"></p>
         * @param cl
         */
        public ClassDescription(Class<?>cl) {
            // TODO get information directly from class.
            this(cl.getName());
        }

        /**
         * <p class="changed_added_4_0"></p>
         * @return the typeParameters
         */
        public String getTypeParameters() {
            return typeParameters;
        }

        /**
         * <p class="changed_added_4_0"></p>
         * @param typeParameters the typeParameters to set
         */
        public void setTypeParameters(String typeParameters) {
            this.typeParameters = typeParameters;
        }

        /**
         * <p class="changed_added_4_0">
         * </p>
         * 
         * @return the name
         */
        public String getName() {
            return name;
        }

        /**
         * <p class="changed_added_4_0"></p>
         * @return package name.
         */
        public String getPackage() {
            int indexOfPeriod = name.lastIndexOf('.');
            if(indexOfPeriod>0){
                return name.substring(0,indexOfPeriod);
            } else {
                return null;
            }
        }

        /**
         * <p class="changed_added_4_0"></p>
         * @return package name.
         */
        public String getSimpleName() {
            int indexOfPeriod = name.lastIndexOf('.');
            if(indexOfPeriod>0){
                return name.substring(indexOfPeriod+1);
            } else {
                return name;
            }
        }


        @Override
        public String toString() {
            return fullName;
        }

        /* (non-Javadoc)
         * @see java.lang.Object#hashCode()
         */
        @Override
        public int hashCode() {
            final int prime = 31;
            int result = 1;
            result = prime * result + ((name == null) ? 0 : name.hashCode());
            return result;
        }

        /* (non-Javadoc)
         * @see java.lang.Object#equals(java.lang.Object)
         */
        @Override
        public boolean equals(Object obj) {
            if (this == obj) {
                return true;
            }
            if (obj == null) {
                return false;
            }
            if (!(obj instanceof ClassDescription)) {
                return false;
            }
            ClassDescription other = (ClassDescription) obj;
            if (fullName == null) {
                if (other.fullName != null) {
                    return false;
                }
            } else if (!fullName.equals(other.fullName)) {
                return false;
            }
            return true;
        }

}
