/*
 * Copyright 2008-2009 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package net.hasor.rsf.container;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import org.more.FormatException;
import org.more.util.StringUtils;
import net.hasor.core.AppContext;
import net.hasor.core.BindInfo;
import net.hasor.core.Hasor;
import net.hasor.core.Provider;
import net.hasor.core.binder.InstanceProvider;
import net.hasor.rsf.RsfBinder;
import net.hasor.rsf.RsfFilter;
import net.hasor.rsf.RsfService;
import net.hasor.rsf.RsfSettings;
import net.hasor.rsf.address.AddressPool;
import net.hasor.rsf.address.InterAddress;
import net.hasor.rsf.address.InterServiceAddress;
import net.hasor.rsf.domain.RsfServiceType;
import net.hasor.rsf.domain.ServiceDomain;
/**
 * 服务注册器
 * @version : 2014年11月12日
 * @author 赵永春(zyc@hasor.net)
 */
abstract class RsfBindBuilder implements RsfBinder {
    protected abstract RsfBeanContainer getContainer();
    protected AddressPool getAddressPool() {
        return getContainer().getAddressPool();
    }
    //
    public void bindFilter(String filterID, RsfFilter instance) {
        this.getContainer().addFilter(filterID, instance);
    }
    public void bindFilter(String filterID, Provider<? extends RsfFilter> provider) {
        this.getContainer().addFilter(filterID, provider);
    }
    public <T> LinkedBuilder<T> rsfService(Class<T> type) {
        return new LinkedBuilderImpl<T>(type);
    }
    public <T> ConfigurationBuilder<T> rsfService(Class<T> type, T instance) {
        return this.rsfService(type).toInstance(instance);
    }
    public <T> ConfigurationBuilder<T> rsfService(Class<T> type, Class<? extends T> implementation) {
        return this.rsfService(type).to(implementation);
    }
    public <T> ConfigurationBuilder<T> rsfService(Class<T> type, Provider<T> provider) {
        return this.rsfService(type).toProvider(provider);
    }
    public <T> ConfigurationBuilder<T> rsfService(Class<T> type, BindInfo<T> bindInfo) {
        return this.rsfService(type).toInfo(bindInfo);
    }
    @Override
    public void updateFlowControl(String flowControl) {
        this.getAddressPool().updateDefaultFlowControl(flowControl);
    }
    @Override
    public void updateArgsRoute(String scriptBody) {
        this.getAddressPool().updateDefaultArgsRoute(scriptBody);
    }
    @Override
    public void updateMethodRoute(String scriptBody) {
        this.getAddressPool().updateDefaultMethodRoute(scriptBody);
    }
    @Override
    public void updateServiceRoute(String scriptBody) {
        this.getAddressPool().updateDefaultServiceRoute(scriptBody);
    }
    //
    private class LinkedBuilderImpl<T> implements LinkedBuilder<T> {
        private final ServiceInfo<T>            serviceDefine;
        private final Map<InterAddress, String> addressMap;
        //
        protected LinkedBuilderImpl(Class<T> serviceType) {
            this.serviceDefine = new ServiceInfo<T>(serviceType);
            this.addressMap = new HashMap<InterAddress, String>();
            RsfSettings settings = getContainer().getEnvironment().getSettings();
            //
            RsfService serviceInfo = new AnnoRsfServiceValue(settings, serviceType);
            ServiceDomain<T> domain = this.serviceDefine.getDomain();
            domain.setServiceType(RsfServiceType.Consumer);
            domain.setBindGroup(serviceInfo.group());
            domain.setBindName(serviceInfo.name());
            domain.setBindVersion(serviceInfo.version());
            domain.setSerializeType(serviceInfo.serializeType());
            domain.setClientTimeout(serviceInfo.clientTimeout());
        }
        //
        @Override
        public ConfigurationBuilder<T> group(String group) {
            Hasor.assertIsNotNull(group, "group is null.");
            if (group.contains("/") == true) {
                throw new FormatException(group + " contain '/'");
            }
            this.serviceDefine.getDomain().setBindGroup(group);
            return this;
        }
        //
        @Override
        public ConfigurationBuilder<T> name(String name) {
            Hasor.assertIsNotNull(name, "name is null.");
            if (name.contains("/") == true) {
                throw new FormatException(name + " contain '/'");
            }
            this.serviceDefine.getDomain().setBindName(name);
            return this;
        }
        //
        @Override
        public ConfigurationBuilder<T> version(String version) {
            Hasor.assertIsNotNull(version, "version is null.");
            if (version.contains("/") == true) {
                throw new FormatException(version + " contain '/'");
            }
            this.serviceDefine.getDomain().setBindVersion(version);
            return this;
        }
        //
        @Override
        public ConfigurationBuilder<T> timeout(int clientTimeout) {
            if (clientTimeout < 1) {
                throw new FormatException("clientTimeout must be greater than 0");
            }
            this.serviceDefine.getDomain().setClientTimeout(clientTimeout);
            return this;
        }
        //
        @Override
        public ConfigurationBuilder<T> serialize(String serializeType) {
            Hasor.assertIsNotNull(serializeType, "serializeType is null.");
            if (serializeType.contains("/") == true) {
                throw new FormatException(serializeType + " contain '/'");
            }
            this.serviceDefine.getDomain().setSerializeType(serializeType);
            return this;
        }
        //
        public ConfigurationBuilder<T> bindFilter(String filterID, RsfFilter instance) {
            this.serviceDefine.addRsfFilter(filterID, instance);
            return this;
        }
        //
        public ConfigurationBuilder<T> bindFilter(String filterID, Provider<? extends RsfFilter> provider) {
            this.serviceDefine.addRsfFilter(filterID, provider);
            return this;
        }
        //
        @Override
        public ConfigurationBuilder<T> to(final Class<? extends T> implementation) {
            Hasor.assertIsNotNull(implementation);
            final AppContext appContext = getContainer().getAppContext();
            return this.toProvider(new Provider<T>() {
                public T get() {
                    return appContext.getInstance(implementation);
                }
            });
        }
        @Override
        public ConfigurationBuilder<T> toInfo(final BindInfo<T> bindInfo) {
            Hasor.assertIsNotNull(bindInfo);
            final AppContext appContext = getContainer().getAppContext();
            return this.toProvider(new Provider<T>() {
                public T get() {
                    return appContext.getInstance(bindInfo);
                }
            });
        }
        @Override
        public ConfigurationBuilder<T> toInstance(T instance) {
            return this.toProvider(new InstanceProvider<T>(instance));
        }
        //
        @Override
        public ConfigurationBuilder<T> toProvider(Provider<T> provider) {
            this.serviceDefine.getDomain().setServiceType(RsfServiceType.Provider);
            this.serviceDefine.setCustomerProvider(provider);
            return this;
        }
        //
        @Override
        public RegisterBuilder<T> bindAddress(String rsfHost, int port) throws URISyntaxException {
            String unitName = getContainer().getEnvironment().getSettings().getUnitName();
            return this.bindAddress(new InterAddress(rsfHost, port, unitName));
        }
        @Override
        public RegisterBuilder<T> bindAddress(String rsfURI, String... array) throws URISyntaxException {
            if (!StringUtils.isBlank(rsfURI)) {
                this.bindAddress(new InterAddress(rsfURI));
            }
            if (array.length > 0) {
                for (String bindItem : array) {
                    this.bindAddress(new InterAddress(bindItem));
                }
            }
            return this;
        }
        @Override
        public RegisterBuilder<T> bindAddress(URI rsfURI, URI... array) {
            if (rsfURI != null && (InterServiceAddress.checkFormat(rsfURI) || InterAddress.checkFormat(rsfURI))) {
                this.bindAddress(new InterAddress(rsfURI));
            }
            if (array.length > 0) {
                for (URI bindItem : array) {
                    if (rsfURI != null && (InterServiceAddress.checkFormat(bindItem) || InterAddress.checkFormat(bindItem))) {
                        this.bindAddress(new InterAddress(bindItem));
                    }
                    throw new FormatException(bindItem + " check fail.");
                }
            }
            return this;
        }
        public RegisterBuilder<T> bindAddress(InterAddress rsfAddress, InterAddress... array) {
            if (rsfAddress != null) {
                this.addressMap.put(rsfAddress, AddressPool.Dynamic);
            }
            if (array.length > 0) {
                for (InterAddress bindItem : array) {
                    if (bindItem == null)
                        continue;
                    this.addressMap.put(bindItem, AddressPool.Dynamic);
                }
            }
            return this;
        }
        //
        @Override
        public RegisterBuilder<T> bindStaticAddress(String rsfHost, int port) throws URISyntaxException {
            String unitName = getContainer().getEnvironment().getSettings().getUnitName();
            return this.bindStaticAddress(new InterAddress(rsfHost, port, unitName));
        }
        @Override
        public RegisterBuilder<T> bindStaticAddress(String rsfURI, String... array) throws URISyntaxException {
            if (!StringUtils.isBlank(rsfURI)) {
                this.bindStaticAddress(new InterAddress(rsfURI));
            }
            if (array.length > 0) {
                for (String bindItem : array) {
                    this.bindStaticAddress(new InterAddress(bindItem));
                }
            }
            return this;
        }
        @Override
        public RegisterBuilder<T> bindStaticAddress(URI rsfURI, URI... array) {
            if (rsfURI != null && (InterServiceAddress.checkFormat(rsfURI) || InterAddress.checkFormat(rsfURI))) {
                this.bindStaticAddress(new InterAddress(rsfURI));
            }
            if (array.length > 0) {
                for (URI bindItem : array) {
                    if (rsfURI != null && (InterServiceAddress.checkFormat(bindItem) || InterAddress.checkFormat(bindItem))) {
                        this.bindStaticAddress(new InterAddress(bindItem));
                    }
                    throw new FormatException(bindItem + " check fail.");
                }
            }
            return this;
        }
        public RegisterBuilder<T> bindStaticAddress(InterAddress rsfAddress, InterAddress... array) {
            if (rsfAddress != null) {
                this.addressMap.put(rsfAddress, AddressPool.Static);
            }
            if (array.length > 0) {
                for (InterAddress bindItem : array) {
                    if (bindItem == null)
                        continue;
                    this.addressMap.put(bindItem, AddressPool.Static);
                }
            }
            return this;
        }
        //
        public RegisterReference<T> register() throws IOException {
            String serviceID = this.serviceDefine.getDomain().getBindID();
            Set<InterAddress> staticSet = new HashSet<InterAddress>();
            Set<InterAddress> dynamicSet = new HashSet<InterAddress>();
            //
            for (Entry<InterAddress, String> ent : this.addressMap.entrySet()) {
                if (ent.getKey() == null) {
                    continue;
                }
                if (StringUtils.equals(AddressPool.Static, ent.getValue())) {
                    staticSet.add(ent.getKey());
                } else if (StringUtils.equals(AddressPool.Dynamic, ent.getValue())) {
                    dynamicSet.add(ent.getKey());
                }
            }
            //
            getContainer().getAddressPool().appendStaticAddress(serviceID, staticSet);
            getContainer().getAddressPool().appendAddress(serviceID, dynamicSet);
            return getContainer().publishService(this.serviceDefine);
        }
        @Override
        public void updateFlowControl(String flowControl) {
            AddressPool pool = getContainer().getAddressPool();
            String serviceID = this.serviceDefine.getDomain().getBindID();
            pool.updateFlowControl(serviceID, flowControl);
        }
        @Override
        public void updateArgsRoute(String scriptBody) {
            AddressPool pool = getContainer().getAddressPool();
            String serviceID = this.serviceDefine.getDomain().getBindID();
            pool.updateArgsRoute(serviceID, scriptBody);
        }
        @Override
        public void updateMethodRoute(String scriptBody) {
            AddressPool pool = getContainer().getAddressPool();
            String serviceID = this.serviceDefine.getDomain().getBindID();
            pool.updateMethodRoute(serviceID, scriptBody);
        }
        @Override
        public void updateServiceRoute(String scriptBody) {
            AddressPool pool = getContainer().getAddressPool();
            String serviceID = this.serviceDefine.getDomain().getBindID();
            pool.updateServiceRoute(serviceID, scriptBody);
        }
    }
}